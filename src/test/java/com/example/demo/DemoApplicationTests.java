package com.example.demo;

import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class DemoApplicationTests {

	@Test
	public void upload() throws Exception {
		try {
			String fileName = "100.pdf";
			String url = "http://111.47.10.117:30010/ocr/recognize_document?pdf=1";
			String path= ResourceUtils.getURL("classpath:static").getPath();
			File targetFile = new File(path+"/100.pdf");
			File newFile = new File(path+"/200.pdf");
			System.out.println(upload(url, targetFile, fileName,newFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static ResponseBody upload(String url, File targetFile, String fileName,File newFile) throws Exception {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .build();
        RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("file", fileName,
						RequestBody.create(MediaType.parse("application/pdf"), targetFile))
				.build();
		Request request = new Request.Builder()
				.addHeader("Content-Type", "application/pdf")
				.url(url)
				.post(requestBody)
				.build();
		long start = System.currentTimeMillis();
		Response response = client.newCall(request).execute();
		long end = System.currentTimeMillis();
		System.out.println("消耗时间---------"+(end-start));
		int code = response.code();
		if (code == 200) {
			byte[] bytes = response.body().bytes();
			BufferedInputStream bin = null;
			FileOutputStream fout = null;
			BufferedOutputStream bout = null;
			try {
				//创建一个将bytes作为其缓冲区的ByteArrayInputStream对象
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				//创建从底层输入流中读取数据的缓冲输入流对象
				bin = new BufferedInputStream(bais);
				//指定输出的文件
				if (!newFile.exists()){
					newFile.createNewFile();
				}
				//创建到指定文件的输出流
				fout= new FileOutputStream(newFile);
				//为文件输出流对接缓冲输出流对象
				bout = new BufferedOutputStream(fout);
				byte [] buffers = new byte[1024];
				int len = bin.read(buffers);
				while(len != -1){
					bout.write(buffers, 0, len);
					len = bin.read(buffers);
				}
				//刷新此输出流并强制写出所有缓冲的输出字节，必须这行代码，否则有可能有问题
				bout.flush();
			}catch(IOException e) {
				e.printStackTrace();
			}finally{
				try {
					bin.close();
					fout.close();
					bout.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		return response.body();
	}


}

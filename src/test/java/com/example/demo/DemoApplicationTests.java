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

@SpringBootTest
class DemoApplicationTests {

	@Test
	public void upload() throws Exception {
		try {
			String fileName = "11111.pdf";
			String url = "http://127.0.0.1:30006/icr/recognize_document?pdf=1";
			String path= ResourceUtils.getURL("classpath:static").getPath();
			File targetFile = new File(path+"/11111.pdf");
			File newFile = new File(path+"/22222.pdf");
			System.out.println(upload(url, targetFile, fileName,newFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	multipart/form-data
	static ResponseBody upload(String url, File targetFile, String fileName,File newFile) throws Exception {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("file", fileName,
						RequestBody.create(MediaType.parse("application/pdf"), targetFile))
				.build();
		Request request = new Request.Builder()
				.header("Authorization", "Client-ID " + UUID.randomUUID())
				.url(url)
				.post(requestBody)
				.build();
		Response response = client.newCall(request).execute();
		int code = response.code();
		InputStream inputStream = response.body().byteStream();
		if (code == 200) {
			OutputStream outStream = new FileOutputStream(newFile);
			byte[]buffer=new byte[1024];
			int readLength;
			while((readLength=inputStream.read(buffer))>0){
				System.out.println(new String(buffer,0,readLength));
				outStream.write(buffer);
			}
			outStream.close();
		}
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		return response.body();
	}


}

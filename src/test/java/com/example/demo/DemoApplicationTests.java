package com.example.demo;

import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.UUID;

@SpringBootTest
class DemoApplicationTests {

	@Test
	public void upload() throws Exception {
		try {
			String fileName = "11111.pdf";
			String url = "http://127.0.0.1:30006/icr/recognize_document?pdf=1";
//			File targetFile = new File("C:\\Users\\Administrator\\Desktop\\11111.pdf");
//			File newFile = new File("C:\\Users\\Administrator\\Desktop\\22222.pdf");
			String path= ResourceUtils.getURL("classpath:static").getPath();
			String path2= ClassUtils.getDefaultClassLoader().getResource("").getPath();
//			.replace("%20"," ").replace('/', '\\');//从路径字符串中取出工程路径
			File targetFile = new File(path+"/11111.pdf");
			File newFile = new File(path+"/22222.pdf");
			System.out.println(upload(url, targetFile, fileName,newFile).string());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static ResponseBody upload(String url, File targetFile, String fileName,File newFile) throws Exception {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("file", fileName,
						RequestBody.create(MediaType.parse("multipart/form-data"), targetFile))
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
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);
			OutputStream outStream = new FileOutputStream(newFile);
			outStream.write(buffer);
		}
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		return response.body();
	}

}

package com.example.demo;

import okhttp3.*;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
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
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
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


	@Test
	public void doc(){

		try {
			String path= ResourceUtils.getURL("classpath:static").getPath();
			XWPFDocument doc = new XWPFDocument();

			XWPFParagraph title = doc.createParagraph();
			XWPFRun run = title.createRun();
//			run.setText("Fig.1 A Natural Scene");
//			run.setBold(true);
//			title.setAlignment(ParagraphAlignment.CENTER);

			String imgFile5 = path+"/5.jpg";
			FileInputStream is5 = new FileInputStream(imgFile5);
			String imgFile6 = path+"/6.jpg";
			FileInputStream is6 = new FileInputStream(imgFile6);
			for (int i=0;i<10;i++){
				run.addPicture(is5, XWPFDocument.PICTURE_TYPE_JPEG, imgFile5, Units.toEMU(200), Units.toEMU(200)); // 200x200 pixels
				run.addBreak();
			}


			FileOutputStream fos = new FileOutputStream(path+"/7.docx");
			doc.write(fos);
			fos.close();
			is5.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	@Test
	public void doc2(){

		try {
			String path= ResourceUtils.getURL("classpath:static").getPath();
			String imgFile5 = path+"/5.jpg";
			XWPFDocument doc = new XWPFDocument();
			XWPFParagraph title = doc.createParagraph();
			XWPFRun run = title.createRun();
			run.setText("特征比对截图比对表");
			run.setBold(true);
			run.setFontSize(26);
			title.setAlignment(ParagraphAlignment.CENTER);
			XWPFTable table = doc.createTable(10, 2);
			//设置表头
			XWPFTableRow row = table.getRow(0);
			row.setHeight(20);
			XWPFTableCell cell0 = row.getCell(0);
			cell0.setWidth("80%");
			List<XWPFParagraph> paragraphs0 = cell0.getParagraphs();
			XWPFParagraph newPara0 = paragraphs0.get(0);
			XWPFRun imageCellRunn0 = newPara0.createRun();
			imageCellRunn0.setText("比对检材切片");
			imageCellRunn0.setBold(true);
			imageCellRunn0.setFontSize(16);
			newPara0.setAlignment(ParagraphAlignment.CENTER);

			XWPFTableCell cell1 = row.getCell(1);
			cell1.setWidth("80%");
			List<XWPFParagraph> paragraphs1 = cell1.getParagraphs();
			XWPFParagraph newPara1 = paragraphs1.get(0);
			XWPFRun imageCellRunn1 = newPara1.createRun();
			imageCellRunn1.setText("比对样本切片");
			imageCellRunn1.setBold(true);
			imageCellRunn1.setFontSize(16);
			newPara1.setAlignment(ParagraphAlignment.CENTER);
			for (int i=0;i< 10;i++){
				for (int j = 0; j< 2; j++){
					if (i==0){
						continue;
					}
					XWPFTableCell cell = table.getRow(i).getCell(j);
					cell.setWidth("80%");
					List<XWPFParagraph> paragraphs = cell.getParagraphs();
					XWPFParagraph newPara = paragraphs.get(0);
					XWPFRun imageCellRunn = newPara.createRun();
					File image = new File(imgFile5);
					FileInputStream is = new FileInputStream(imgFile5);
					imageCellRunn.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, image.getName(), Units.toEMU(100), Units.toEMU(100)); // 200x200 pixels
					imageCellRunn.setBold(true);
					newPara.setAlignment(ParagraphAlignment.CENTER);
				}
			}
			FileOutputStream fos = new FileOutputStream(path+"/8.docx");
			doc.write(fos);
			fos.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}


}

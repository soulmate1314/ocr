package com.example.demo;

import com.alibaba.fastjson.JSONArray;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.general.find.PdfTextFind;
import com.spire.pdf.general.find.PdfTextFindCollection;
import com.spire.pdf.security.GraphicMode;
import com.spire.pdf.security.PdfCertificate;
import com.spire.pdf.security.PdfCertificationFlags;
import com.spire.pdf.security.PdfSignature;
import okhttp3.*;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.List;
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


	@Test
	public  void testpdf(){
		try {
			String path= ResourceUtils.getURL("classpath:static").getPath();
			System.out.println("path----------------------"+path+"/11111.pdf");
			File targetFile = new File(path+"/222.pdf");
			//加载PDF文档
//			D:/workspace/idea/myworld/ocr/target/classes/static/11111.pdf
			PdfDocument doc = new PdfDocument();
//			doc.loadFromFile("11111.pdf");
			byte[] bytesByFile = getBytesByFile(targetFile);
			doc.loadFromBytes(bytesByFile);
			//加载pfx证书，及证书秘钥
			PdfCertificate cert = new PdfCertificate(path+"/yansir.pfx","yansir");

			//查找第一页中所有的“Germany”
			PdfTextFindCollection text1 = doc.getPages().get(0).findText("日 期：");
			PdfTextFind[] finds = text1.getFinds();

//获取第一个“Germany”出现的位置
			Point2D point2D = finds[0].getPosition();
			Dimension2D size = finds[0].getSize();
			System.out.println(point2D+"-----------"+ JSONArray.toJSONString(point2D));
			System.out.println(size+"-----------"+ JSONArray.toJSONString(size));

			//添加数字签名到指定页面，并设置其位置和大小
			PdfSignature signature = new PdfSignature(doc, doc.getPages().get(0), cert, "MySignature");
			Rectangle2D rect = new Rectangle2D.Float();
			double x = doc.getPages().get(0).getActualSize().getWidth() - 340;
			double y = doc.getPages().get(0).getActualSize().getHeight() - 150;
			System.out.println(x+"---------------"+y);
//			rect.setFrame(point2D, size);
            rect.setFrame(new Point2D.Float((float)353,(float)712), new Dimension(36, 9));
			signature.setBounds(rect);

			//设置签名为图片加文本模式
			signature.setGraphicMode(GraphicMode.Sign_Image_And_Sign_Detail);

			//设置签名的内容
			signature.setNameLabel("签字者：");
			signature.setName("Mia");
			signature.setContactInfoLabel("联系电话：");
			signature.setContactInfo("02881705109");
			signature.setDateLabel("日期：");
			signature.setDate(new java.util.Date());
			signature.setLocationInfoLabel("地点：");
			signature.setLocationInfo("成都");
			signature.setReasonLabel("原因：");
			signature.setReason("文档所有者");
			signature.setDistinguishedNameLabel("DN: ");
			signature.setDistinguishedName(signature.getCertificate().get_IssuerName().getName());
//			signature.setSignImageSource(PdfImage.fromFile(path+"/5.jpg"));

			//设置签名的字体
			//设置文档权限为禁止更改
			signature.setDocumentPermissions(PdfCertificationFlags.Forbid_Changes);
			signature.setCertificated(true);

			//保存文档
			doc.saveToFile("4.pdf");
			doc.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static byte[] getBytesByFile(File file) {
		try {
			//获取输入流
			FileInputStream fis = new FileInputStream(file);

			//新的 byte 数组输出流，缓冲区容量1024byte
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
			//缓存
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			//改变为byte[]
			byte[] data = bos.toByteArray();
			//
			bos.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}

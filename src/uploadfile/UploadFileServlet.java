package uploadfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(urlPatterns = { "/uploadServlet" })
public class UploadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("doget!");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.获取和创建保存文件的最终目录和临时目录
		String savePath = req.getSession().getServletContext().getRealPath("/WEB-INF/upload"); //保存文件的服务器上的绝对路径
		String tempPath = req.getSession().getServletContext().getRealPath("/WEB-INF/temp"); //临时目录
		File tempFile = new File(tempPath);
		if (!tempFile.exists()) {
			tempFile.mkdirs();   //如果临时目录不存在的话，我用代码的方式，创建一个新目录
		}
		
		//2.创建一个DiskFileItemFactory工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(100*1024); //单位：字节 , 100KB,上传的文件<100KB,放在内存中，>100KB,放tempPath
		factory.setRepository(tempFile); //设置上传文件的临时目录
		
		//3.创建一个文件上传解析器
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory); //得到文件的解析器
		servletFileUpload.setFileSizeMax(20*1024*1024); //限制上传单个文件的大小在20M以内
		servletFileUpload.setHeaderEncoding("UTF-8"); //防止上传的中文信息是乱码
		servletFileUpload.setFileSizeMax(40*1024*1024); //限制多个文件同时上传的时候，总大小最大值40M
		servletFileUpload.setProgressListener(new ProgressListener() {
			@Override
			public void update(long yUploadFileSize, long uploadFileSize, int item) {
				System.out.println("上传文件总大小为：" + uploadFileSize + ",已经上传文件的大小：" + yUploadFileSize);
			}
		});
		
		//4.判断提交上来的数据是否是上传表单的数据，是不是Multipart编码方式   ServletFileUpload.isMultipartContent(request)
		if (!ServletFileUpload.isMultipartContent(req)) {
			return;
		}
		
		//5.使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合
		OutputStream out = null;
		InputStream in = null;
		try {
			List<FileItem> fileList = servletFileUpload.parseRequest(req);
			if (fileList != null && fileList.size() > 0) {
				for(FileItem fileItem:fileList) {
					if (fileItem.isFormField()) {
						//将普通表单域的键值对显示出来
						System.out.println("普通的表单项，name为：" + fileItem.getFieldName());
						System.out.println("普通的表单项，value为：" + fileItem.getString("UTF-8"));
					}else {
						//是文件域，通过fileItem,拿到上传上来的文件的各种信息，和文件的文本
						String fileName = fileItem.getName(); //拿到文件的名字 xxx.doc  xxx.txt
						if (fileName == null || fileName.trim().equals("")) {
							continue;
						}
						//注意事项：IE拿到的fileName是带有绝对路径，D:\abc\xxx.doc  ;   火狐浏览器拿到的  xxx.doc
						fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
						String fileType = fileItem.getContentType(); //拿到文件的类型image/jpg
						long fileSize = fileItem.getSize(); //拿到文件的总大小
						//拿到文件后缀名
						String fileEx = fileName.substring(fileName.lastIndexOf(".")+1);
						//验证后缀的合法性
						if (fileEx.equals("rar") || fileEx.equals("zip")) {
							throw new RuntimeException("禁止上传压缩文件！");
						}
						//将文件流写入保存的目录中(生成新的文件名，避免一个目录中文件太多而生成新的存储目录)
						String saveFileName = makeFileName(fileName);
						String realSavePath = makePath(saveFileName,savePath);
						//先创建一个输出流
						out = new FileOutputStream(realSavePath + "\\" + saveFileName);
						in = fileItem.getInputStream();
						//建立缓存区，做一个搬运文件数据流的勺子
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len=in.read(buffer)) > 0){
							out.write(buffer, 0, len);
						}
						in.close();
						out.close();
					}
				}
			}
		} catch (FileUploadBase.FileSizeLimitExceededException e) {
			System.out.println("单个文件大小超出限制！");
		}catch (FileUploadBase.SizeLimitExceededException e) {
			System.out.println("总文件大小超出限制！");
		}catch (Exception e) {
			System.out.println("上传文件失败！");
		}finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	private String makePath(String saveFileName, String savePath) {
		//拿到文件名字，在内存当中地址，hashCode值
		int hashCode = saveFileName.hashCode();
		int dir1 = hashCode&0xf; //dir1的值，这个与运算结果范围0-15
		int dir2 = (hashCode >> 4)&0xf; //得到的结果还是0-15范围内
		//用dir1,dir2构造我的新的存储文件的目录
		String dir = savePath + "\\" + dir1 + "\\" + dir2;
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir;
	}

	private String makeFileName(String fileName) {
		//uuid
		return UUID.randomUUID().toString()+"_"+fileName;
	}
}

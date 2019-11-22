package uploadfile;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
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
		//fileupload.jar
		//1.创建一个工厂类
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//factory工厂类，可以设置很多对上传文件的限制内容
		//public DiskFileItemFactory(int sizeThreshold,File repository)
		//sizeThreshold:服务器里内存，有限资源，上传文件，传过来的文件放到内存里，快
		//              50G的大文件，内存放不下，内存溢出错误，操作死机
		//              限制：sizeThreshold临界值，600KB  上传的文件，小于600KB，把接收到的整个文件
		//              放在内存，程序可以直接从内存当中拿到整个文件
		//              传来的文件大于600KB，把传过来的文件分成很多部分，放在磁盘上的某个临时文件夹
		//              程序需要整个文件，就去内存和临时文件夹里的临时文件中去取
		//repository:设置临时文件夹的
		
		//2.创建request请求的解析器
		ServletFileUpload sfu = new ServletFileUpload(factory);
		//sfu这个解析器，也是可以设置对上传文件的限制内容：可以是单个文件的最大容量，也可是多个文件的总大小
//		sfu.setFileSizeMax(fileSizeMax);
//		sfu.setSizeMax(sizeMax);
		
		//3.解析器来解析request请求,解析需要捕获异常
		try {
			List<FileItem> list = sfu.parseRequest(req);
			for(FileItem fileItem:list) {
				//fileItem:就是封装有一个一个form提交过来的表单项：普通表单项 / 文件域表单项
				//第一步，判断这个表单项是不是普通表单项
				if(fileItem.isFormField()) {
					//像普通表单一样处理
					String name = fileItem.getFieldName(); //拿到这个域的名字
					String value = fileItem.getString(); //拿到这个域里的值
				}else {
					//上传上来的文件
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}
}

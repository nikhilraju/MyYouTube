package fileop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.catalina.ServerFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FilenameUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

import video.util.*;

/**
 * Servlet implementation class FileUploadServlet
 */
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	AmazonS3Client s3;
	AWSCredentials credentials;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUploadServlet() {
		super();
		try {
			credentials = new PropertiesCredentials(
					FileUploadServlet.class
							.getResourceAsStream("../AwsCredentials.properties"));
			s3 = new AmazonS3Client(credentials);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		File file;
		int maxFileSize = -1;
		int maxMemSize = -1;
		ServletContext context = getServletContext();
		String filePath = context.getInitParameter("file-upload");
		String bucketName = context.getInitParameter("s3-bucket");
		String repPath = context.getInitParameter("rep-path");
		PrintWriter out = response.getWriter();
		// Verify the content type
		String contentType = request.getContentType();
		if ((contentType.indexOf("multipart/form-data") >= 0)) {

			DiskFileItemFactory factory = new DiskFileItemFactory();
			// maximum size that will be stored in memory
			factory.setSizeThreshold(maxMemSize);
			// Location to save data that is larger than maxMemSize.
			factory.setRepository(new File(repPath));

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			// maximum file size to be uploaded.
			upload.setSizeMax(maxFileSize);
			try {
				// Parse the request to get file items.
				List fileItems = upload.parseRequest(request);

				// Process the uploaded file items
				Iterator i = fileItems.iterator();

				out.println("<html>");
				out.println("<head>");
				out.println("<title>JSP File upload</title>");
				out.println("</head>");
				out.println("<body>");
				Video v = new Video();
				while (i.hasNext()) {
					FileItem fi = (FileItem) i.next();
					if (!fi.isFormField()) {
						// Get the uploaded file parameters
						String fieldName = fi.getFieldName();
						String fileName = fi.getName();
						String extension = fi.getContentType();
						boolean isInMemory = fi.isInMemory();
						long sizeInBytes = fi.getSize();
						file = File.createTempFile("temp", ".txt");
						file.deleteOnExit();
						fi.write(file);
						PutObjectRequest por = new PutObjectRequest(bucketName,
								fileName, file);
						por.setCannedAcl(CannedAccessControlList.PublicRead);
						// put object - bucket, key, value(file)
						s3.putObject(por);
						v.setCreatedAt(new Date(System.currentTimeMillis()));
						v.setCreateTime(new java.sql.Time(System
								.currentTimeMillis()));
						v.setVname(fileName);
						v.setExtension(extension);
						new ConnectDB().createVideo(v);
						response.sendRedirect("/MyYouTube/index.jsp?myvideos=true&userid="
								+ v.getCreatedBy());
					} else {
						if ("description".equalsIgnoreCase(fi.getFieldName()))
							v.setDescription(fi.getString());
						else if ("title".equalsIgnoreCase(fi.getFieldName()))
							v.setTitle(fi.getString());
						else if ("userid".equalsIgnoreCase(fi.getFieldName()))
							v.setCreatedBy(Integer.parseInt(fi.getString()));
					}
				}
				out.println("</body>");
				out.println("</html>");
			} catch (Exception ex) {
				out.println(ex.toString());
				System.out.println(ex);
			}
		} else {
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet upload</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<p>No file uploaded</p>");
			out.println("</body>");
			out.println("</html>");
		}
	}
}

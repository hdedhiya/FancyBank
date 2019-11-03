import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLConnection {
	//change the "test" here to the name of your own database
	private static final String URL="jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";
	
	private static final String NAME="root";//username
	private static final String PASSWORD="?????";//password
	public java.sql.Connection conn = null;
	
	public void TheSqlConnection(){
	    //1.load the driver
	    try {
	        Class.forName("com.mysql.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        System.out.println("Failed to load the driver");
	        e.printStackTrace();
	    }try {
	        conn = DriverManager.getConnection(URL, NAME, PASSWORD);
	        System.out.println("Successfully connected!");    
	
	    }catch (SQLException e){
	        System.out.println("Failed to connect to mysql!");
	        //check your username and password
	        e.printStackTrace();
	    }
	}
	
	public List<Test> getSelect() {
		//just for testing
		String sql = "select * from testTb"; 
	    PreparedStatement pst = null;
	    List<Test> list = new ArrayList<Test>();
	    try {
	        pst = (PreparedStatement) conn.prepareStatement(sql);
	        java.sql.ResultSet rs = pst.executeQuery();
	        while (rs.next()) {
	            int testId = Integer.parseInt(rs.getString("testID"));
	            String testName = rs.getString("testName");
	            Test test = new Test(testId, testName);
	            list.add(test);
	        }
	    }catch (Exception e) {
	        System.out.printf("don't get any");
	   	}
	    return list;
	}
	
	public void delete(){
	    
	}
	
	public void update(){
	    
	}
	
	public void insert(){
	    
	}
	
	public void close(){
		//shut down the connection
		if(conn!=null){
	        try {
	            conn.close();
	        }catch (SQLException e){
	        // TODO Auto-generated catch block
	            e.printStackTrace();
	            conn = null;
	        }
	     }
	}
}

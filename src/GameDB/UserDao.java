package GameDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class UserDao {

	private Connection conn;    //DB 커넥션(연결) 객체
    private static final String USERNAME = "root";   //DB 접속시 ID
    private static final String PASSWORD = "1234";    //DB 접속시 패스워드
    //DB접속 경로(스키마=데이터베이스명)설정
    private static final String URL = "jdbc:mysql://localhost:3306/mydb"; 
    PreparedStatement pstmt = null;
    HashSet<String> roomList = new HashSet<>();
    
    //생성자
    public UserDao() {
    	// connection객체를 생성해서 DB에 연결함.
        try {
            System.out.println("생성자");
           //동적 객체를 만들어줌 
            Class.forName("com.mysql.jdbc.Driver"); 
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("드라이버 로딩 성공!!");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("드라이버 로드 실패!!");
        }
    }    
    
    //DB에 데이터를 저장하는 메서드
    public void insertUser(User user) {
        //쿼리문 준비
       String sql = "insert into usertbl values(?,?);";
        //DB에 값을 넣어주는 클래스(원래 Statement클래스로 하였으나,
       //사용하기가 즉 가독성이 좋치않아서, PreparedStatement클래스를
       //많이 사용한다.
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getId());   //첫 번째 ? 매핑
            pstmt.setString(2, user.getRoom()); //두 번째 ? 매핑
//            pstmt.setString(3, User.getGrade());//세 번째 ? 매핑
            //쿼리문 실행하라.
            pstmt.executeUpdate();
            System.out.println("User데이터 삽입 성공!");
        } catch (SQLException e) {            
           System.out.println("User데이터 삽입 실패!");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null && !pstmt.isClosed())
                    pstmt.close();
            } catch (SQLException e) {                
                e.printStackTrace();
            }
        }
    }
    
    public void updateUser(String id, String room) {
        String sql = "update usertbl set room = ? where id = ?;";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, room);
            pstmt.setString(2, id);
//            pstmt.setString(3, User.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(pstmt != null && !pstmt.isClosed())
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }    
 
    public void updateUser(User user) {
        String sql = "update usertbl set room = ? where id = ?;";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getRoom());
//            pstmt.setString(3, User.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(pstmt != null && !pstmt.isClosed())
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //조건에 맞는 행을 DB에서 삭제하는 메서드
    public void deleteUser(String id) {
        String sql = "delete from User where id = ?;";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null && !pstmt.isClosed())
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //조건에 맞는 행을 DB에서 1개 행만 가져오는 메서드
    public User selectOne(String id) {
        String sql = "select * from User where id = ?;";
        User re = new User();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            //pstmt.setString(2, id);  //and 조건이 붙을 때마다 추가한다. 
            ResultSet rs = pstmt.executeQuery();
            //select한 결과는 ResultSet에 담겨 리턴된다.
            if (rs.next()) {  //가져올 행이 있으면 true, 없으면 false
                re.setId(rs.getString("id"));
                re.setRoom(rs.getString("room"));
//                re.setGrade(rs.getString("grade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null && !pstmt.isClosed())
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return re;
    }
    //User테이블에 존재하는 모든 행을 가져오는 메서드임
    public List<User> selectAll() {
       
       String sql = "select * from User;";
        List<User> list = new ArrayList<User>();
        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet re = pstmt.executeQuery();
 
            while (re.next()) {   //가져올게 있느냐?
            	User s = new User();
                s.setId(re.getString("ID"));
                s.setRoom(re.getString("ROOM"));
//                s.setGrade(re.getString("grade"));
                list.add(s);   //List<User>에다가 추가함.
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null && !pstmt.isClosed())
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    
//    public boolean checkId(User user) throws SQLException {
//		Connection conn = DAO.getConnect();
//		boolean possible = false;
//
//		String sql = "select * from game_user where id = ? ";
//		pstmt = conn.prepareStatement(sql);
//
//		try {
//			pstmt.setString(1, user.getUserid());
//			// 4. �떎�뻾
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				return possible = true;
//			} else {
//				return possible = false;
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			// 5. �뿰寃고빐�젣
//			DAO.close(conn);
//		}
//		return possible;
//	}
    public boolean select(String id) throws SQLException {
    	String sql = "select * from usertbl where id = ?;";
    	
    	pstmt = conn.prepareStatement(sql);
    	
    	pstmt.setString(1, id);
    	ResultSet rs = pstmt.executeQuery();
    	
    	if(rs.next()) {
    		return true;
    	}
    	else
    		return false;   	
    	
    }
    
    public boolean checkUser(User user) throws SQLException {
    	String sql = "select * from usertbl where id = ?;";
    	User re = new User();
    	pstmt = conn.prepareStatement(sql);
    	
    	pstmt.setString(1, user.getId());
    	ResultSet rs = pstmt.executeQuery();    	
    	
    	if(rs.next()) {
    		re.setRoom(rs.getString("room"));
//    		System.out.println("re 방이름 " + re.getRoom());
    		if(re.getRoom().equals(user.getRoom()))
    			return false;
    		else
    			return true;
    	} else
    		return false;  		
    		
    }
//    public User selectOne(String id) {
//        String sql = "select * from User where id = ?;";
//        User re = new User();
//        try {
//            pstmt = conn.prepareStatement(sql);
//            pstmt.setString(1, id);
//            //pstmt.setString(2, id);  //and 조건이 붙을 때마다 추가한다. 
//            ResultSet rs = pstmt.executeQuery();
//            //select한 결과는 ResultSet에 담겨 리턴된다.
//            if (rs.next()) {  //가져올 행이 있으면 true, 없으면 false
//                re.setId(rs.getString("id"));
//                re.setRoom(rs.getString("room"));
////                re.setGrade(rs.getString("grade"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (pstmt != null && !pstmt.isClosed())
//                    pstmt.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return re;
//    }
    
    public HashSet<String> roomList() throws SQLException {
    	String sql = "select distinct room from usertbl;";
    	pstmt = conn.prepareStatement(sql);
    	
    	ResultSet rs = pstmt.executeQuery();
    	while(rs.next())
    		roomList.add(rs.getString("room"));
    	
    	return roomList;    	
    }
}

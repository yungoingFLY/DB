import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Login extends JFrame {
	
	JLabel loginName = new JLabel("이름");
	JLabel loginNum = new JLabel("사원번호");
	
	JTextField tFieldName = new JTextField();
	JTextField tFieldNum = new JTextField();
	
	JButton buttonLogin = new JButton("로그인");
	
	private Connection db;
	
	public Login(Connection db) {
		
		this.db = db;
		this.setLayout(null);
		loginName.setBounds(15, 15, 100, 30);
		tFieldName.setBounds(100, 15, 100, 30);
		loginNum.setBounds(15, 50, 100, 30);
		tFieldNum.setBounds(100, 50, 100, 30);
		buttonLogin.setBounds(220, 30, 100, 30);
	
		this.add(loginName);
		this.add(tFieldName);
		this.add(loginNum);
		this.add(tFieldNum);
		this.add(buttonLogin);
	
		
		this.setBounds(100, 100, 350, 130);
		this.setTitle("사원 로그인");
		this.setVisible(true);
	}
	public String[] workerLogin(){
		String loginSet[] = null;
		try{
			String name = tFieldName.getText();
			String num = tFieldNum.getText();
			
			String sql = "SELECT * FROM worker WHERE name = '"+name+"' and id = '"+num+"'";
			PreparedStatement stmt = db.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			loginSet = new String[2];
			loginSet[0] = rs.getString("name");
			loginSet[1] = rs.getString("grade");  
		}catch(Exception e){
			e.printStackTrace();
		}
		return loginSet;
		
	}

}

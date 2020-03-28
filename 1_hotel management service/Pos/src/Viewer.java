import java.awt.BorderLayout;
import java.util.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Viewer implements ActionListener {
	
	Calendar cal = Calendar.getInstance();
	String today = cal.get(Calendar.YEAR) + "/"
			+ (cal.get(Calendar.MONTH)+1) + "/"
			+ cal.get(Calendar.DATE);

	
	Connection db;
	String sql;
	PreparedStatement stmt;
	ResultSet rs;
	int customer_id = 1000;
	int menu_id = 1000;

	int dataLoaded = 0;

	String authority; 
	String currentStaff; 
	JFrame frame = new JFrame();

	
	JLabel title = new JLabel("식당 주문 관리");


	JMenuBar bar = new JMenuBar();
	
	JMenu menu = new JMenu("Menu");
	
	JMenuItem open = new JMenuItem("Open");
	JMenuItem login = new JMenuItem("Log in");
	
	String filePath;

	
	JPanel main_panel = new JPanel();
	JPanel title_panel = new JPanel();
	JPanel grid_panel = new JPanel();
	JPanel table_panel = new JPanel();
	JPanel order_panel = new JPanel();
	JPanel menu_panel = new JPanel();
	JPanel sign_panel = new JPanel();

	
	JButton[] tables = new JButton[20];

	
	JTextArea t_order = new JTextArea();
	String t_order_string = "ㅂㅈㄷㅂㅈㄷ\n";
	JLabel l_customer_name = new JLabel("고객명");
	JTextField f_customer_name = new JTextField();
	JLabel l_table_name = new JLabel("테이블명");
	JComboBox<String> c_table_name = new JComboBox<String>();
	JButton b_order = new JButton("주문");
	JButton b_cancle = new JButton("취소");
	JButton b_pay = new JButton("결제");

	JButton[] menus = new JButton[20];

	JTabbedPane tp = new JTabbedPane();
	JPanel tab_customer = new JPanel();
	JLabel l_customer_name4 = new JLabel("고객명");
	JTextField f_customer_name4 = new JTextField();
	JButton b_sign = new JButton("가입");
	JButton b_find = new JButton("조회");
	JTextArea t_customer = new JTextArea();
	
	JPanel tab_sales = new JPanel();
	JLabel l_period = new JLabel("매출");
	JComboBox<String> c_date = new JComboBox<String>();
	JTextArea t_sales_area = new JTextArea();
	
	JPanel tab_staff = new JPanel();
	JLabel l_staff_name = new JLabel("직원명");
	JTextField f_staff_name = new JTextField();
	JButton b_add_staff = new JButton("추가");
	JButton b_find_staff = new JButton("조회");
	JTextArea t_staff_area = new JTextArea();
	
	JPanel tab_menu = new JPanel();
	JLabel l_menu_name = new JLabel("메뉴명");
	JTextField f_menu_name = new JTextField();
	JButton b_menu_add = new JButton("추가");
	JButton b_find_m = new JButton("조회");
	JTextArea t_menu_area = new JTextArea();

	
	public Viewer(Connection db) {
		this.db = db;
		// JMenuBar
		open.setMnemonic('O');
		login.setMnemonic('L');
		open.addActionListener(this);
		login.addActionListener(this);
		menu.add(open); 
		menu.add(login);
		bar.add(menu); 

		// title_panel
		title.setFont(new Font("Bold", 1, 40));
		title_panel.add(title);
		title_panel.setBackground(Color.WHITE);
		title_panel.setBorder(new LineBorder(Color.BLACK, 3));

		// grid_panel
		grid_panel.setLayout(new GridLayout(2, 2));

	
		// table_panel
		table_panel.setBorder(new TitledBorder("테이블 현황"));
		table_panel.setLayout(new GridLayout(4, 5));
		
		for (int i = 0; i < 20; i++) {
			tables[i] = new JButton((i+1)+"");
			tables[i].setBackground(Color.WHITE);
			tables[i].setFont(new Font("Bold", 1, 20));
			tables[i].addActionListener(this);
			table_panel.add(tables[i]);
		}

		
		// order_panel
		order_panel.setBorder(new TitledBorder("주문내역"));
		order_panel.setLayout(null);
		t_order.setBorder(new LineBorder(Color.gray, 2));
		t_order.setEditable(false);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(t_order);
		for (int i = 0; i < 20; i++) {
			c_table_name.addItem((i + 1) +"");
		}
		c_table_name.addActionListener(this);
		b_order.addActionListener(this);
		b_order.setBackground(Color.WHITE);
		b_cancle.addActionListener(this);
		b_cancle.setBackground(Color.WHITE);
		b_pay.addActionListener(this);
		b_pay.setBackground(Color.WHITE);
		
		scroll.setBounds(15, 18, 200, 330);
		l_customer_name.setBounds(230, 15, 100, 30);
		f_customer_name.setBounds(230, 50, 100, 30);
		l_table_name.setBounds(230, 85, 100, 30);
		c_table_name.setBounds(230, 110, 100, 30);
		b_order.setBounds(230, 170, 100, 30);
		b_cancle.setBounds(230, 220, 100, 30);
		b_pay.setBounds(230, 270, 100, 30);
		// order_panel
		order_panel.add(scroll);
		order_panel.add(l_customer_name);
		order_panel.add(f_customer_name);
		order_panel.add(l_table_name);
		order_panel.add(c_table_name);
		order_panel.add(b_order);
		order_panel.add(b_cancle);
		order_panel.add(b_pay);

		
		// menu_panel
		menu_panel.setBorder(new TitledBorder("메뉴"));
		menu_panel.setLayout(new GridLayout(10, 2));
		
		for (int i = 0; i < 20; i++) {
			menus[i] = new JButton();
			menus[i].addActionListener(this);
			menus[i].setBackground(Color.WHITE);
			menu_panel.add(menus[i]);
		}

		
		// sign_panel
		sign_panel.setBorder(new TitledBorder("등록/조회"));
		sign_panel.setLayout(new BorderLayout());
		
		tab_customer.setLayout(null);
		l_customer_name4.setBounds(15, 15, 100, 30);
		f_customer_name4.setBounds(15, 50, 100, 30);
		b_sign.setBounds(180, 50, 60, 30);
		b_find.setBounds(250, 50, 60, 30);
		t_customer.setBounds(15, 90, 300, 200);
		t_customer.setBorder(new LineBorder(Color.gray, 2));
		b_sign.addActionListener(this);
		b_find.addActionListener(this);
		b_sign.setBackground(Color.WHITE);
		b_find.setBackground(Color.WHITE);
		tab_customer.add(l_customer_name4);
		tab_customer.add(f_customer_name4);
		tab_customer.add(b_sign);
		tab_customer.add(b_find);
		tab_customer.add(t_customer);
		
		tab_sales.setLayout(null);
		l_period.setBounds(15, 15, 100, 30);
		c_date.setBounds(150, 15, 100, 30);
		t_sales_area.setBounds(15, 50, 300, 240);
		t_sales_area.setBorder(new LineBorder(Color.gray, 2));
		tab_sales.add(l_period);
		tab_sales.add(c_date);
		tab_sales.add(t_sales_area);
		
		tab_staff.setLayout(null);
		l_staff_name.setBounds(15, 15, 100, 30);
		f_staff_name.setBounds(15, 50, 100, 30);
		b_add_staff.setBounds(150, 50, 90, 30);
		b_find_staff.setBounds(250, 50, 60, 30);
		t_staff_area.setBounds(15, 90, 300, 200);
		t_staff_area.setBorder(new LineBorder(Color.gray, 2));
		b_add_staff.addActionListener(this);
		b_find_staff.addActionListener(this);
		b_add_staff.setBackground(Color.WHITE);
		b_find_staff.setBackground(Color.WHITE);
		tab_staff.add(l_staff_name);
		tab_staff.add(f_staff_name);
		tab_staff.add(b_add_staff);
		tab_staff.add(b_find_staff);
		tab_staff.add(t_staff_area);
		
		tab_menu.setLayout(null);
		l_menu_name.setBounds(15, 15, 100, 30);
		f_menu_name.setBounds(15, 50, 120, 30);
		b_menu_add.setBounds(150, 50, 90, 30);
		b_find_m.setBounds(250, 50, 60, 30);
		t_menu_area.setBounds(15, 90, 300, 200);
		t_menu_area.setBorder(new LineBorder(Color.gray, 2));
		b_menu_add.addActionListener(this);
		b_find_m.addActionListener(this);
		b_menu_add.setBackground(Color.WHITE);
		b_find_m.setBackground(Color.WHITE);
		tab_menu.add(l_menu_name);
		tab_menu.add(f_menu_name);
		tab_menu.add(b_menu_add);
		tab_menu.add(b_find_m);
		tab_menu.add(t_menu_area);
		
		tp.addTab("고객", tab_customer);
		tp.addTab("매출", tab_sales);
		tp.addTab("직원", tab_staff);
		tp.addTab("메뉴", tab_menu);
		sign_panel.add(tp, BorderLayout.CENTER);

		
		grid_panel.add(table_panel);
		grid_panel.add(order_panel);
		grid_panel.add(menu_panel);
		grid_panel.add(sign_panel);

		// main_panel
		main_panel.setLayout(new BorderLayout());
		main_panel.add(title_panel, BorderLayout.NORTH);
		main_panel.add(grid_panel, BorderLayout.CENTER);

		
		frame.setJMenuBar(bar); // JMenuBar
		frame.add(main_panel);

	
		frame.setTitle("식당 관리 시스템");
		frame.setBounds(0, 0, 700, 850);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		try {
			this.menuUpdate();
			this.checkDataIsOpen();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("???? ??¼? : Æ??? ¿?±¸");
			JOptionPane.showMessageDialog(null, "로그인 되어습니다.");
		}
	}

	//dbtable을 만드는 함수
	public void readFile(File textile) throws SQLException{
	      Scanner scanner = null;
	         
	      int indexcount = 0;     
	      int check = 0;
	      
	      try { 
	         scanner = new Scanner(textile);
	         while(scanner.hasNextLine()){
	            String test = scanner.nextLine().toString();
	            //System.out.println("count : " + test);
	            try{
	            	int number = Integer.parseInt(test);
	            	check ++;     	
	            	
	            	if(check == 1){
	            		System.out.println("회원목록입니다");
	            		while(indexcount < number){
	            		test = scanner.nextLine().toString();            		
	            		StringTokenizer token = new StringTokenizer(test,"\t");
	            		while(token.hasMoreTokens()){     
	            			 String guestName = token.nextToken();
	            			 String guestID = token.nextToken();
	            			 String guestBirthDay = token.nextToken();
	            			 String guestLevel = token.nextToken();
	            			 
	            			 System.out.println("guestname = " + guestName + ", guestID = " + guestID+", guestBirthday = " + guestBirthDay+", guestLevel = " + guestLevel);
	            		 }            		 
	            		indexcount ++ ;
	            		}            		
	            		indexcount = 0;
	            		//System.out.println("---------------------------");
	            	}
	            	else if(check == 2){
	            		//System.out.println("직원목록입니다");
	            		while(indexcount < number){
	                		test = scanner.nextLine().toString();                		
	                		StringTokenizer token = new StringTokenizer(test,"\t");
	                		 while(token.hasMoreTokens()){     
	                			 String staffName = token.nextToken();
	                			 String staffLevel = token.nextToken();               			
	                			 
	                			System.out.println("staffName = " + staffName+", staffLevel = " + staffLevel );
	                		 }                		 
	                		indexcount ++ ;
	                		}                		
	                		indexcount = 0;
	                		//System.out.println("---------------------------");
	            	}
	            	else if(check == 3){
	            		//System.out.println("메뉴목록입니다");
	            		while(indexcount < number){
	                		test = scanner.nextLine().toString();	                		
	                		StringTokenizer token = new StringTokenizer(test,"\t");
	                		 while(token.hasMoreTokens()){     
	                			 String foodname = token.nextToken();
	                			 int foodprice = Integer.parseInt(token.nextToken());
	                			 
	                			 System.out.println("foodname = " + foodname+", price = " + foodprice );
	                		 }                		 
	                		indexcount ++ ;
	                		}                		
	                		indexcount = 0;
	                		//System.out.println("---------------------------");
	            	}
	            }
	            catch(Exception e){
	            	JOptionPane.showMessageDialog(null, "파일 읽기에 실패했습니다. : 다시 시도해 주세요" );
	            }
	         }                 
	       }                     
	      catch (FileNotFoundException e) {
	         JOptionPane.showMessageDialog(null, "파일 읽기에 실패했습니다. : 다시 시도해 주세요" );
	      }
	      catch (Exception e) {
	         JOptionPane.showMessageDialog(null, "파일 읽기에 실패했습니다. : 다시 시도해 주세요" );      
	      }      
	      finally{
	         scanner.close();
	      }
	   }
		public void createTable() throws SQLException{	
			
		String customer = "CREATE TABLE guest (\n"
			+ "guestname	    varchar(10) not null,\n"
			+ "guestID		    varchar(5)  not null, \n"
			+ "guestBirthDay  	varchar(5)  not null, \n"
			+ "guestLevel       varchar(10) not null, \n"
			+ "totalpayment     Integer not null, \n"
			+ "primary key(guestname) \n"
					+ ")";	
		
		String worker = "CREATE TABLE staff (\n"
			+ "staffname	varchar(10) not null,\n"
			+ "staffLevel   varchar(10) not null, \n"
			+ "totalpayment     Integer not null, \n"
			+ "primary key(staffname) \n"
					+ ")";	
				
		String menu = "CREATE TABLE foodmenu (\n"
			+ "foodid	    Integer not null,\n"
			+ "foodname	    varchar(30) not null,\n"
			+ "foodprice	Integer  not null, \n"
			+ "primary key(foodname) \n"
					+ ")";	
		
		String order = "CREATE TABLE payment (\n"
			+ "paydate         date	    not null, \n"
			+ "guestname	varchar(10) not null,\n"
			+ "foodname	    varchar(30) not null,\n"
			+ "foodprice	Integer  not null \n"
			
					+ ")";	
		
		String sql = "";	
		//for문으로 table 생성을 위해 table이름, query 배열을 생성
		try{
			 String[] tablename = {"guest","staff","foodmenu","payment"};
		 String[] query = {customer, worker, menu,order};
		 for(int i = 0; i<tablename.length; i++){		
			 //db에 이미 table이 생성이 되어있는지를 check, 없다면 생성, 있다면 생성하지 않음
				 if(dbtableexist(tablename[i]) == false){
					 System.out.println(tablename[i]);
					 sql = query[i];
					 stmt = db.prepareStatement(sql);
			    	 rs=stmt.executeQuery();	         
			         db.commit();
					}			
			 }         
		}
		catch(SQLException e){	    		
				JOptionPane.showMessageDialog(null,"테이블생성에 문제가 발생했습니다 : " +  e);	 
			    	db.rollback();	    	
		    }
			finally{
		    	 stmt.close();
			     rs.close();
		    }
		}
		
		//createTable 함수 실행시 DB에 table의 존재 유무를 알려주는 함수	
		public boolean dbtableexist(String tablename) throws SQLException{
		    String sql = "Select TABLE_NAME from user_tables where table_name='"+tablename.toUpperCase()+"' ";  
		 boolean existTable = false;
		 stmt = db.prepareStatement(sql);
		 rs=stmt.executeQuery();
		 
		while (rs.next()){
		    if(rs.getString("table_name").equals(tablename.toUpperCase())){
			existTable = true;	// db에 이미 테이블 존재시 true : table 생성할 필요가 없음                            
		            }               
		        }
		        stmt.close();
		     	rs.close(); 
		        return existTable;
		}
		
	// actionPerformed



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	
		// Data File Open
		if (e.getSource() == open) {
			if (dataLoaded == 1) {
				System.out.println("??¹? data.txt¸? º?·?¿?½?´?´?. (?ßº¹±???)");
			}

			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("?Ø½ºÆ®Æ???(.txt) ¶?´? SQL Schema(.sql)", "txt", "sql");
			chooser.setFileFilter(filter);
			int check = chooser.showOpenDialog(null);
			if (check != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "올바른 파일 형식이 아닙니다.", "실패", JOptionPane.WARNING_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(null, "데이터를 성공적으로 가져왔습니다.");
				filePath = chooser.getSelectedFile().getPath();
				try {
			
					if (filePath.contains("pos_schema.sql")) {
						this.openSchema();
						JOptionPane.showMessageDialog(null,"Schema¸? º?·?¿??¸¹?·? ??????¸? º?·?¿?¾ß ??´?´?.");
					}
					
					else {
						if (dataLoaded == 0) {
							this.openTXTtoSQL();
						}
						else {
							JOptionPane.showMessageDialog(null, "¿?·? ¹ø º?·?¿? ¼? ¾ø½?´?´?.");
						}
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		else if (e.getSource() == login) {
			if (dataLoaded == 0) {
				JOptionPane.showMessageDialog(null, "??????¸? º?·?¿?¾ß ·?±×???? ¼? ??½?´?´?.");
			}
			else {
				new Login(db, this);
				f_customer_name.setText("");
				f_customer_name4.setText("");
				t_customer.setText("");
				t_sales_area.setText("");
				f_staff_name.setText("");
				t_staff_area.setText("");
				f_menu_name.setText("");
				t_menu_area.setText("");
			}
		}

		
		else if (e.getSource() == tables[0]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("1");
		}
		else if (e.getSource() == tables[1]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("2");
		}
		else if (e.getSource() == tables[2]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("3");
		}
		else if (e.getSource() == tables[3]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("4");
		}
		else if (e.getSource() == tables[4]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("5");
		}
		else if (e.getSource() == tables[5]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("6");
		}
		else if (e.getSource() == tables[6]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("7");
		}
		else if (e.getSource() == tables[7]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("8");
		}
		else if (e.getSource() == tables[8]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("9");
		}
		else if (e.getSource() == tables[9]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("10");
		}
		else if (e.getSource() == tables[10]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("11");
		}
		else if (e.getSource() == tables[11]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("12");
		}
		else if (e.getSource() == tables[12]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("13");
		}
		else if (e.getSource() == tables[13]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("14");
		}
		else if (e.getSource() == tables[14]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("15");
		}
		else if (e.getSource() == tables[15]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("16");
		}
		else if (e.getSource() == tables[16]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("17");
		}
		else if (e.getSource() == tables[17]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("18");
		}
		else if (e.getSource() == tables[18]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("19");
		}
		else if (e.getSource() == tables[19]) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			c_table_name.setSelectedItem("20");
		}



		else if (e.getSource() == c_table_name) {
			try {
				String showing = showTableOrder();
				t_order.setText(showing);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == b_order) {
			if (t_order.getText().contains("<?ß°¡??¹®>")) {
				String table_num = (String) c_table_name.getSelectedItem();
				StringTokenizer st = new StringTokenizer(t_order.getText(), "\n");
				st.nextToken();
				while (st.hasMoreTokens()) {
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "\t");
					sql = "insert into orders values('"
							+ st2.nextToken() + "','" // ¸Þ´º??¸§
							+ today + "'," // ³???
							+ st2.nextToken() + "," // °¡°?
							+ table_num + "," // ?×??º? ¹ø??
							+ "null," // ±¸¸??? : ¾Æ?÷ ??·? ??¿? ¾ø?½
							+ "1)"; // ???? ?×??º??? ????; 1:???½, 0:¾ø?½
					System.out.println(sql);
					try {
						stmt = db.prepareStatement(sql);
						stmt.executeUpdate();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						String showing = showTableOrder();
						t_order.setText(showing);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				int num = new Integer(table_num).intValue();
				tables[num-1].setBackground(Color.YELLOW);
				t_order_string = "<?ß°¡??¹®>\n";
				JOptionPane.showMessageDialog(null, "??¹®??¾?½?´?´?.");
			}
		}
		else if (e.getSource() == b_cancle) {
			t_order_string = "<?ß°¡??¹®>\n";
			t_order.setText("");
			String table_num = (String)c_table_name.getSelectedItem();
			sql = "delete from orders where dates = '"
					+ today + "' and table_num = "
					+ table_num + " and status = 1";
			try {
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int num = new Integer(table_num).intValue();
			tables[num-1].setBackground(Color.WHITE);
			JOptionPane.showMessageDialog(null, "¸ð?? ³?¿ª?? ??¼???¾?½?´?´?.");
		}
		// °???
		else if (e.getSource() == b_pay) {
			// ·?±×?? ??¾? ???? ¾??? °æ¿? °????? ¼? ¾ø??·? ??
			if (currentStaff == null) {
				JOptionPane.showMessageDialog(null, "???? ??¿ø ¶?´? °?¸®??°¡ ·?±×?? ??¾? ???? ¾?¾Æ °????? ¼? ¾ø½?´?´?.");
			}
			// ?×??º?¿¡ ??¹®³?¿ª?? ¾ø?? °æ¿? °??? º?°¡
			else if (!t_order.getText().contains("?? ??°?")) {
				JOptionPane.showMessageDialog(null, "?? ?×??º?¿¡ ³?¿ª?? ?¸?????? ¾?¾Æ °????? ¼? ¾ø½?´?´?.");
			}
			else {
				String table_num = (String) c_table_name.getSelectedItem();
				String buyer = f_customer_name.getText();
				int sum_price = 0;
				String grade = "";
				if (buyer.equals("")) {
					buyer = "º??¸¿ø";
				}

				// °?????·?´???¶÷??°?°´¸????????????¼???Øº¸±?
				// customer?? ??¸§???? ±¸?Ø¼­ ¹?¸® ?®??
				sql = "select name from customer";
				boolean checkName = false;
				try {
					stmt = db.prepareStatement(sql);
					rs = stmt.executeQuery();
					while (rs.next()) {
						if (buyer.equals(rs.getString("name"))) {
							checkName = true;
						}
					}
				} catch (SQLException e3) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null,"¸?·??? º?·?¿? ¼? ¾ø½?´?´?.");
				}
				if (!checkName) {
					JOptionPane.showMessageDialog(null, "?¸?????? ¾?´? °?°´¸???´?´?.\n°¡????¿? ??½?±? ¹?¶ø´?´?.");
				}
				else {
					// °????? ±?¾× ±¸??±?
					sql = "select sum(price) from orders where dates = '"
							+ today + "' and status = 1 and table_num = "
							+ table_num;
					try {
						stmt = db.prepareStatement(sql);
						rs = stmt.executeQuery();
						rs.next();
						sum_price = rs.getInt("sum(price)");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// °?????·?´? °?°´´??? ??±Þ?? ?¼??
					if (!buyer.equals("º??¸¿ø")) {
						sql = "select grade from customer where name = '"
								+ buyer + "'";
						String buyer_grade = "";
						try {
							stmt = db.prepareStatement(sql);
							rs = stmt.executeQuery();
							rs.next();
							buyer_grade = rs.getString("grade");
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (buyer_grade.equals("Gold")) {
							sum_price = sum_price / 100 * 70;
						}
						else if (buyer_grade.equals("Silver")) {
							sum_price = sum_price / 100 * 80;
						}
						else if (buyer_grade.equals("Bronze")) {
							sum_price = sum_price / 100 * 90;
						}
						System.out.println("????¾×" + buyer_grade);
					}

					// ???? ·?±×?? ??¿ø?? ½??? ?ß°¡??±?
					sql = "update worker set record = record + "
							+ sum_price + "where name = '"
							+ currentStaff + "'";
					try {
						stmt = db.prepareStatement(sql);
						stmt.executeUpdate();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// °?°´´??? ??¸§¿¡ ±¸¸?¾× ?ß°¡??±?
					// ¸???, °?°´´??? amount¸? ±¸?Ø¿?¼­ ??±Þ º?°æ?? ?¼??
					sql = "select amount from customer where name = '"
							+ buyer + "'";
					try {
						stmt = db.prepareStatement(sql);
						rs = stmt.executeQuery();
						rs.next();
						int amount = rs.getInt("amount");
						amount += sum_price;
						if (amount >= 1000000) {
							grade = "Gold";
						}
						else if (amount >= 500000) {
							grade = "Silver";
						}
						else if (amount >= 300000) {
							grade = "Bronze";
						}
						else {
							grade = "Normal";
						}
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					sql = "update customer set amount = amount + "
							+ sum_price + ", grade = '"
							+ grade + "' where name = '"
							+ buyer + "'";
					try {
						stmt = db.prepareStatement(sql);
						stmt.executeUpdate();
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					// ?×??º? ???? 0?¸·? ?¹¸®±?
					sql = "update orders set status = 0, buyer = '"
							+ buyer + "' where dates = '"
							+ today + "' and status = 1 and table_num = "
							+ table_num;
					try {
						stmt = db.prepareStatement(sql);
						stmt.executeUpdate();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					JOptionPane.showMessageDialog(null, "°?????¾?½?´?´?.");
					t_order.setText("°?????¾?½?´?´?.");
					f_customer_name.setText("");
					int num = new Integer(table_num).intValue();
					tables[num-1].setBackground(Color.WHITE);
				}
			}
		}


		else if (e.getSource() == menus[0]) {
			menuAddOrder(menus[0]);
		}
		else if (e.getSource() == menus[1]) {
			menuAddOrder(menus[1]);
		}
		else if (e.getSource() == menus[2]) {
			menuAddOrder(menus[2]);
		}
		else if (e.getSource() == menus[3]) {
			menuAddOrder(menus[3]);
		}
		else if (e.getSource() == menus[4]) {
			menuAddOrder(menus[4]);
		}
		else if (e.getSource() == menus[5]) {
			menuAddOrder(menus[5]);
		}
		else if (e.getSource() == menus[5]) {
			menuAddOrder(menus[5]);
		}
		else if (e.getSource() == menus[6]) {
			menuAddOrder(menus[6]);
		}
		else if (e.getSource() == menus[7]) {
			menuAddOrder(menus[7]);
		}
		else if (e.getSource() == menus[8]) {
			menuAddOrder(menus[8]);
		}
		else if (e.getSource() == menus[9]) {
			menuAddOrder(menus[9]);
		}
		else if (e.getSource() == menus[10]) {
			menuAddOrder(menus[10]);
		}
		else if (e.getSource() == menus[11]) {
			menuAddOrder(menus[11]);
		}
		else if (e.getSource() == menus[12]) {
			menuAddOrder(menus[12]);
		}
		else if (e.getSource() == menus[13]) {
			menuAddOrder(menus[13]);
		}
		else if (e.getSource() == menus[14]) {
			menuAddOrder(menus[14]);
		}
		else if (e.getSource() == menus[15]) {
			menuAddOrder(menus[15]);
		}
		else if (e.getSource() == menus[16]) {
			menuAddOrder(menus[16]);
		}
		else if (e.getSource() == menus[17]) {
			menuAddOrder(menus[17]);
		}
		else if (e.getSource() == menus[18]) {
			menuAddOrder(menus[18]);
		}
		else if (e.getSource() == menus[19]) {
			menuAddOrder(menus[19]);
		}

		
		else if (e.getSource() == b_sign) {
			if (authority == null) {
				JOptionPane.showMessageDialog(null, "¸??? ·?±×?? ??¿? ??½?±? ¹?¶ø´?´?.");
			}
			else if (authority.equals("Supervisor")) {
				new AddCustomer(db);
			}
			else if (authority.equals("Staff")) {
				JOptionPane.showMessageDialog(null, "Staff´? ??·? ±????? ¾ø½?´?´?.");
			}
		}
		else if (e.getSource() == b_find) {
			try {
				this.findCustomer();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// 4-2. ¸???ÆÐ³?
		else if (e.getSource() == c_date) {
			if (authority.equals("Supervisor")) {
				String result = "";
				String selectedDate = (String) c_date.getSelectedItem();
				// SQL - ?? ¸??? ±¸??±?
				sql = "select sum(price) from orders where dates = '"
						+ selectedDate + "' and status = 0";
				System.out.println(sql);
				if (selectedDate != null) {
					try {
						stmt = db.prepareStatement(sql);
						rs = stmt.executeQuery();
						rs.next();
						result += "?? ¸??? : " + rs.getString("sum(price)");
						result += "\n------------------------------\n";

						
						sql = "with count_menu(menu, value) as "
								+ "(select menu, count(menu) "
								+ "from orders "
								+ "where status = 0 "
								+ "group by menu) "
								+ "select menu from count_menu where value = "
								+ "(select max(value) from count_menu)";
						stmt = db.prepareStatement(sql);
						rs = stmt.executeQuery();
						rs.next();
						result += "°¡?? ¸¹?? Æ?¸° ¸Þ´º\n: " + rs.getString("menu") + "\n\n";
						System.out.println(sql);

						// SQL - °¡?? ??°? Æ?¸° ¸Þ´º ±¸??±?
						sql = "with count_menu(menu, value) as "
								+ "(select menu, count(menu) "
								+ "from orders "
								+ "where status = 0 "
								+ "group by menu) "
								+ "select menu from count_menu where value = "
								+ "(select min(value) from count_menu)";
						stmt = db.prepareStatement(sql);
						rs = stmt.executeQuery();
						rs.next();
						result += "°¡?? ??°? Æ?¸° ¸Þ´º\n: " + rs.getString("menu") + "\n";
						System.out.println(sql);

						result += "------------------------------\n";
						// SQL - ¼±?? ³??? ??±????? ´??? ¸???
						sql = "select sum(price) from orders where dates <= '"
								+ selectedDate + "'";
						stmt = db.prepareStatement(sql);
						rs = stmt.executeQuery();
						rs.next();
						result += "´??? ¸??? : " + rs.getString("sum(price)");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
					t_sales_area.setText(result);
				}
			}
			else if (authority.equals("Staff")) {
				JOptionPane.showMessageDialog(null, "Staff´? ¸??? ³?¿ª?? º¼ ¼? ¾ø½?´?´?.");
			}
		}

		// 4-3. ?÷¿øÆÐ³?
		else if (e.getSource() == b_add_staff) {
			if (authority == null) {
				JOptionPane.showMessageDialog(null, "¸??? ·?±×?? ??¿? ??½?±? ¹?¶ø´?´?.");
			}
			else if (authority.equals("Supervisor")) {
				new AddWorker(db);
			}
			else if (authority.equals("Staff")) {
				JOptionPane.showMessageDialog(null, "Staff´? ??·? ±????? ¾ø½?´?´?.");
			}
		}
		else if (e.getSource() == b_find_staff) {
			try {
				this.findStaff();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// 4-4. ¸Þ´ºÆÐ³?
		else if (e.getSource() == b_menu_add) {
			sql = "select count(menu) from menu";
			try {
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				rs.next();
				int num = rs.getInt("count(menu)");
				if (num == 20) {
					JOptionPane.showMessageDialog(null, "??¹? ¸Þ´º°¡ 20°³ ??·???¾? ??½?´?´?.");
				}
				else {
					if (authority == null) {
						JOptionPane.showMessageDialog(null, "¸??? ·?±×?? ??¿? ??½?±? ¹?¶ø´?´?.");
					}
					else if (authority.equals("Supervisor")) {
						new AddMenu(db, this);
					}
					else if (authority.equals("Staff")) {
						JOptionPane.showMessageDialog(null, "Staff´? ??·? ±????? ¾ø½?´?´?.");
					}
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == b_find_m) {
			try {
				this.findMenu();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}




	// ¸Þ¼?????

	// ½º?°¸¶ º?·?¿?±?
	public void openSchema() throws SQLException, FileNotFoundException {
		File file = new File(filePath);
		FileReader fr = new FileReader(file);
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(fr);
		String s = new String();
		try {
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			br.close();

			String[] inst = sb.toString().split(";");

			Statement st = db.createStatement();

			for (int i = 0; i < inst.length; i++)
			{
				if (!inst[i].trim().equals(""))
				{
					st.executeUpdate(inst[i]);
					System.out.println(">>"+inst[i]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void openTXTtoSQL() throws SQLException {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filePath));
			
			String read = br.readLine();
			int line_num = new Integer(read).intValue();
			String tok = "";
			for (int i = 0; i < line_num; i++) {
				read = br.readLine();
				tok = tok + read +"\t"; 
			}
			StringTokenizer st = new StringTokenizer(tok, "\t");
			for (int i = 0; i < line_num; i++) {
				
				sql = "select customer_id from customer";
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					customer_id = rs.getInt("customer_id");
				}
				customer_id++;
				String name = st.nextToken();
				String born = st.nextToken();
				String phone = st.nextToken();
				String grade = st.nextToken();
				String amount = "0";
			
				if (grade.equals("Gold")) {
					amount = "1000000";
				}
				else if (grade.equals("Silver")) {
					amount = "500000";
				}
				else if (grade.equals("Bronze")) {
					amount = "300000";
				}
				
				sql = "insert into customer values('"
						+ name + "'," // name
						+ customer_id + ",'" // customer_id
						+ born + "'," // born
						+ phone + ",'" // phone
						+ grade + "'," // grade
						+ amount + ")"; // amount
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
			}
			
			read = br.readLine();
			line_num = new Integer(read).intValue();
			tok = "";
			int worker_id = 1001;
			for (int i = 0; i < line_num; i++) {
				read = br.readLine();
				tok = tok + read + '\t';
			}
			st = new StringTokenizer(tok, "\t");
			for (int i = 0; i < line_num; i++) {
				sql = "insert into worker values('"
						+ st.nextToken() + "',"
						+ worker_id + ",'"
						+ st.nextToken() + "',"
						+ "0)";
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
				worker_id++;
			}
			
			read = br.readLine();
			line_num = new Integer(read).intValue();
			tok = "";
			for (int i = 0; i < line_num; i++) {
				read = br.readLine();
				tok = tok + read + '\t';
			}
			st = new StringTokenizer(tok, "\t");
			for (int i = 0; i < line_num; i++) {
				// menu?? id ?®??½??°±?
				sql = "select menu_id from menu";
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				while (rs.next()) {
					menu_id = rs.getInt("menu_id");
				}
				menu_id++;
				sql = "insert into menu values('"
						+ st.nextToken() + "','" 
						+ menu_id + "'," 
						+ st.nextToken() + ")";
				stmt = db.prepareStatement(sql);
				stmt.executeUpdate();
			}
			
			JOptionPane.showMessageDialog(null, "¸ð?? ??????¾?½?´?´?.");
			this.menuUpdate();
			this.dataLoaded = 1; 
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// ¸Þ´º¹?Æ° ¾÷????Æ®
	public void menuUpdate() throws SQLException {
		sql = "select menu from menu";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		for (int i = 0; rs.next(); i++) {
			menus[i].setText(rs.getString("menu"));
		}
	}

	// °?°´?¶?¸
	public void findCustomer() throws SQLException {
		boolean checkName = false;
		// customer?? ??¸§???? ±¸?Ø¼­ ¹?¸® ?®??
		sql = "select name from customer";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		String name = f_customer_name4.getText().replaceAll("\\s","");
		while (rs.next()) {
			if (name.equals(rs.getString("name"))) {
				checkName = true;
			}
		}
		if (f_customer_name4.getText().equals("")) {
			t_customer.setText("¾Æ¹?°??? ??·????? ¾?¾?½?´?´?.");
		}
		else if (checkName) {
			sql = "select * from customer where name = '"
					+ f_customer_name4.getText() + "'";
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			System.out.println(sql);
			rs.next();
			String result = "";
			result += "고객명 : " + rs.getString("name") + "\n";
			result += "생일 : " + rs.getString("born") + "\n";
			result += "전화번호 : " + rs.getString("phone") + "\n";
			result += "고객등급 : " + rs.getString("grade") + "\n";
			result += "총 구매금액 : " + rs.getString("amount") + "\n";
			t_customer.setText(result);
		}
		else {
			t_customer.setText("°??? °?°? ¾ø?½");
		}
	}

	// ¸Þ´º ?¶?¸
	public void findMenu() throws SQLException {
		boolean checkName = false;
		// ¸Þ´º??¸§ ¹?¸® ±¸??±?
		sql = "select menu from menu";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		String menu = f_menu_name.getText().replaceAll("\\s","");
		while (rs.next()) {
			if (menu.equals(rs.getString("name"))) {
				checkName = true;
			}
		}
		if (f_menu_name.getText().equals("")) {
			t_menu_area.setText("¾Æ¹?°??? ??·????? ¾?¾?½?´?´?.");
		}
		else if (checkName) {
			sql = "select * from menu where menu = '"
					+ f_menu_name.getText() + "'";
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			System.out.println(sql);
			rs.next();
			String result = "";
			result += "메뉴명 : " + rs.getString("menu") + "\n";
			result += "가  격 : " + rs.getString("price") + "\n";
			t_menu_area.setText(result);
		}
		else {
			t_menu_area.setText("°??? °?°? ¾ø?½");
		}
	}

	// ?÷¿ø??¸§??±?
	public void findStaff() throws SQLException {
		boolean checkName = false;
		// ?÷¿ø??¸§ ¹?¸® ±¸??±?
		sql = "select name from worker";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		String name = f_staff_name.getText().replaceAll("\\s","");
		while (rs.next()) {
			if (name.equals(rs.getString("name"))) {
				checkName = true;
			}
		}
		if (f_staff_name.getText().equals("")) {
			t_staff_area.setText("¾Æ¹?°??? ??·????? ¾?¾?½?´?´?.");
		}
		else if (checkName) {
			sql = "select * from worker where name = '"
					+ f_staff_name.getText() + "'";
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			System.out.println(sql);
			rs.next();
			String result = "";
			result += "직원명 : " + rs.getString("name") + "\n";
			result += "직   급 : " + rs.getString("grade") + "\n";
			result += "총실적 : " + rs.getString("record") + "\n";
			t_staff_area.setText(result);
		}
		else {
			t_staff_area.setText("°??? °?°? ¾ø?½");
		}
	}

	// ¸??? ¾Æ???? ¼±?¤??±?
	public void getSalesItem() throws SQLException {
		sql = "select distinct dates from orders where status = 0 order by dates";
		System.out.println(sql);
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			c_date.addItem(rs.getString("dates").substring(0, 10));
		}
		c_date.addActionListener(this);
	}

	// ¸Þ´º ¹?Æ° ´­·?¼­ ³?¿ª ?ß°¡??±?
	public void menuAddOrder(JButton menu_button) {
		if (!menu_button.getText().equals("")) {
			try {
				sql = "select menu, price from menu where menu = '"
						+ menu_button.getText() + "'";
				System.out.println(sql);
				stmt = db.prepareStatement(sql);
				rs = stmt.executeQuery();
				rs.next();
				t_order_string += rs.getString("menu") + '\t' + rs.getString("price") + "\n";
				t_order.setText(t_order_string);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	// ?×??º? ¼±?? ½?¿¡ º¸¿???±?
	public String showTableOrder() throws SQLException {
		String result = "";
		String table_num = (String)c_table_name.getSelectedItem();
		System.out.println(table_num);
		sql = "select menu, price from orders where dates = '"
				+ today + "' and table_num = " + table_num
				+ " and status = 1";
		System.out.println(sql);
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		while (rs.next()) {
			result += rs.getString("menu") + "\t"
					+ rs.getString("price") + "\n";
		}
		result += "\n------------------------------\n";
		sql = "select sum(price) from orders where dates = '"
				+ today + "' and table_num = " + table_num
				+ " and status = 1";
		stmt = db.prepareStatement(sql);
		rs = stmt.executeQuery();
		rs.next();
		result += "?? ??°?\t" + rs.getString("sum(price)");
		if (rs.getString("sum(price)") == null) {
			result = "³?¿ª?? ?¸?????? ¾?½?´?´?.";
		}
		return result;
	}

	public void checkDataIsOpen() {
		try {
			sql = "select * from menu";
			stmt = db.prepareStatement(sql);
			rs = stmt.executeQuery();
			rs.next();
			String check = rs.getString("menu");
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "??????¸? º?·?¿? ???? ¾ø½?´?´?.");
			e.printStackTrace();
		}
	}
}

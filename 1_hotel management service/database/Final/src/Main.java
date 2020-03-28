import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Main extends JFrame {
	JMenuBar bar = new JMenuBar();
	JMenu menu = new JMenu("Menu");
	JMenuItem open = new JMenuItem("Open");
	JMenuItem login = new JMenuItem("Log in");

	JLabel title = new JLabel("식당 주문 관리");
	JPanel grid = new JPanel(new GridLayout(2, 2));

	TableView tableView;
	OrderView orderView;
	MenuView menuView;
	TotalView totalView;
	
	Addmenu addmenu;

	private String loginSet[] = null;
	private static Connection db;

	private void connectDB(String name, String pass) {
		try {
			// JDBC Driver Loading
			Class.forName("oracle.jdbc.OracleDriver");
			db = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", name, pass);
			System.out.println("Success Connect Database");

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fail Connect Database");
			System.out.println("SQLException: " + e);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	Main(String name, String pass) {
		connectDB(name, pass);
		this.setLayout(new BorderLayout());
		// JMenuBar
		open.setMnemonic('O');
		login.setMnemonic('L');
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// Data File Open

				JFileChooser chooser = new JFileChooser();
				int check = chooser.showOpenDialog(null);
				if (check != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null, "올바른 파일 형식이 아닙니다.", "실패", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "데이터를 성공적으로 가져왔습니다.");

					try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
						setLoginSet(null);
						droptable();
						createtable();
						String line = br.readLine();
						int n = Integer.valueOf(line);
						// Insert customer
						int id = 1000;
						int amount = 0;
						System.out.println("Customer");
						for (int i = 0; i < n; i++) {
							line = br.readLine();
							String split[] = line.split("\t");
							inserCustomertData(split[0], id, split[1], split[2], split[3], amount);
							id++;
						}
						// Insert Worker
						id = 1000;
						n = Integer.parseInt(br.readLine());
						System.out.println("Worker");
						for (int i = 0; i < n; i++) {
							line = br.readLine();
							String split[] = line.split("\t");
							insertWorkerData(split[0], id, split[1]);
							id++;
						}
						// Insert Menu
						id = 1000;
						n = Integer.parseInt(br.readLine());
						System.out.println("Menu");
						for (int i = 0; i < n; i++) {
							line = br.readLine();
							String split[] = line.split("\t");
							insertMenuData(split[0], id, split[1]);
							id++;
						}

						menuView.updateView();
					} catch (Exception err) {
						System.out.println(err);
					}
				}

			}
		});
		login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Login login = new Login(db);
				login.buttonLogin.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loginSet = login.workerLogin();
						if (loginSet[0] != null) {
							setLoginSet(loginSet);
							JOptionPane.showMessageDialog(null, loginSet[0] + "님이 로그인되었습니다.");
							orderView.setLogin(loginSet);
							login.dispose();
						} else {
							JOptionPane.showMessageDialog(null, "로그인에 실패 했습니다.");
						}

					}
				});
			}
		});

		menu.add(open);
		menu.add(login);
		bar.add(menu);
		this.setJMenuBar(bar); // JMenuBar

		// Title
		title.setFont(new Font("Bold", 1, 40));
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setBackground(Color.WHITE);
		title.setBorder(new LineBorder(Color.BLACK, 3));
		this.add(title, BorderLayout.NORTH);

		// View Init
		tableView = new TableView(db);
		orderView = new OrderView(db);
		menuView = new MenuView(db);
		totalView = new TotalView(db);

		orderView.setMenuView(menuView);
		orderView.setTableView(tableView);
		orderView.setTotalView(totalView);
		menuView.setOrderView(orderView);
		tableView.setOrderView(orderView);

		totalView.setTableView(tableView);
		totalView.setMenuView(menuView);
		
		menuView.setAddmenu(addmenu);

		grid.setLayout(new GridLayout(2, 2));
		grid.add(tableView);
		grid.add(orderView);
		grid.add(menuView);
		grid.add(totalView);
		this.add(grid, BorderLayout.CENTER);

		this.setTitle("식당 관리 시스템");
		this.setBounds(0, 0, 700, 850);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	public void droptable() {
		try {
			System.out.println("droptable");
			String sql = "DROP TABLE customer";
			PreparedStatement stmt = db.prepareStatement(sql);
			stmt.executeUpdate();

			sql = "DROP TABLE worker";
			stmt = db.prepareStatement(sql);
			stmt.executeUpdate();

			sql = "DROP TABLE menu";
			stmt = db.prepareStatement(sql);
			stmt.executeUpdate();

			sql = "DROP TABLE orderlist";
			stmt = db.prepareStatement(sql);
			stmt.executeUpdate();

			sql = "DROP TABLE total";
			stmt = db.prepareStatement(sql);
			stmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public void createtable() {
		try {
			System.out.println("Create Customer");
			String customer = "CREATE TABLE customer (" + "NAME	    varchar(10) not null,"
					+ "ID		varchar(5)  not null, " + "PONE     varchar(5)	not null,"
					+ "BIRTH  	varchar(5)  not null, " + "GRADE    varchar(10) not null, "
					+ "SALES     Integer Default 0)";

			Statement createStmt = db.createStatement();
			createStmt.executeUpdate(customer);

			System.out.println("Create Worker");
			String worker = "CREATE TABLE worker (" + "NAME	varchar(10) not null," + "ID   varchar(10) not null, "
					+ "GRADE varchar(10) not null," + "SALES     Integer not null)";

			createStmt = db.createStatement();
			createStmt.executeUpdate(worker);

			System.out.println("Create menu");
			String menu = "CREATE TABLE menu (" + "NAME    varchar(30) not null," + "ID	    Integer not null,"
					+ "PRICE	Integer  not null)";

			createStmt = db.createStatement();
			createStmt.executeUpdate(menu);

			System.out.println("Create order");
			;
			String orderlist = "CREATE TABLE orderlist(" + "MENUNAME varchar(30) not null,"
					+ "MENUPRICE Integer not null, " + "TABLENUM Integer not null)";

			createStmt = db.createStatement();
			createStmt.executeUpdate(orderlist);

			System.out.println("Create total");
			String total = "CREATE TABLE total (" + "ORDERDATE         date	    not null, "
					+ "MENUNAME	    varchar(30) not null, " + "MENUPRICE	Integer  not null)";

			createStmt = db.createStatement();
			createStmt.executeUpdate(total);

			System.out.println("DONE Create table");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void inserCustomertData(String name, int id, String phone, String birth, String grade, int amount) {
		try {
			amount = 0;
			if (grade.toLowerCase().equals("gold"))
				amount = 1000000;
			else if (grade.toLowerCase().equals("silver"))
				amount = 500000;
			else if (grade.toLowerCase().equals("bronze"))
				amount = 300000;

			String sql = "INSERT into customer values (" + "'" + name + "', " + "" + id + ", " + "'" + phone + "', "
					+ "'" + birth + "', " + "'" + grade + "'," + "" + amount + ")";
			PreparedStatement stmt = db.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertWorkerData(String name, int id, String grade) {
		try {
			String sql = "INSERT into worker values (" + "'" + name + "', " + "" + id + ", " + "'" + grade + "', " + 0
					+ ")";
			PreparedStatement stmt = db.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertMenuData(String name, int id, String price) {
		try {
			String sql = "INSERT into menu values (" + "'" + name + "'," + "" + id + "," + "'" + price + "')";
			PreparedStatement stmt = db.prepareStatement(sql);
			stmt.executeUpdate();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setLoginSet(String loginSet[]) {
		System.out.println(loginSet);
		this.loginSet = loginSet;
		tableView.setLoginTable(loginSet);
		orderView.setLoginOrder(loginSet);
		menuView.setLoginMenu(loginSet);
		totalView.setLoginTotal(loginSet);
	}
}


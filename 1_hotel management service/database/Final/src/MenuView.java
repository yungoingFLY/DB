import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class MenuView extends JPanel {
	JButton menus[] = new JButton[20];
	
	private Connection db;
	
	private OrderView orderView = null;
	private Addmenu addmenu;
	private String loginSet[] = null;
	
	MenuView(Connection db){
		this.db = db;
		this.setBorder(new TitledBorder("�޴�"));
		this.setLayout(new GridLayout(10, 2));
		
		for (int i = 0; i < 20; i++) {
			menus[i] = new JButton();
			menus[i].setBackground(Color.WHITE);
			menus[i].setEnabled(false);
			menus[i].addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String MenuName = ((JButton)e.getSource()).getText();
					String result = orderView.tAreaOrder.getText();
					int price = orderView.getPrice(MenuName);
					result += (MenuName + "\t" + price +"\n"); 
					orderView.tAreaOrder.setText(result);
					orderView.addOrderToList(MenuName);
					orderView.showOrderView();
			
				}
			});
			this.add(menus[i]);
		}
		updateView();
		setLoginMenu(loginSet);
	}
	
	public void updateView() {
		try {
			String sql = "SELECT name FROM menu";
			PreparedStatement stmt = db.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			int n = 0;
			while(rs.next()){
				menus[n].setText(rs.getString("name"));
				n++;
			}
			stmt.close();
			setLoginMenu(loginSet);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setLoginMenu(String loginSet[]) {
        this.loginSet = loginSet;
        if(loginSet != null) {
            for(JButton menu : menus) {
                if(!menu.getText().equals("")) menu.setEnabled(true);
            }
        }
        else {
            for(JButton menu : menus) {
                menu.setEnabled(false);
            }
        }
    }

	
	public void setOrderView(OrderView orderView) {
		this.orderView = orderView;
	}
	public void setAddmenu(Addmenu addmenu) {
		this.addmenu = addmenu;
	}
	public void setLogin(String loginSet[]){
		this.loginSet = loginSet;
	}
	
}

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConnecttoDB extends JFrame {
	JLabel labelName = new JLabel("�̸�");
	JLabel labelPass = new JLabel("��й�ȣ");

	JTextField tFieldName = new JTextField();
	JPasswordField pFieldPass = new JPasswordField();

	JButton buttonLogin = new JButton("�α���");

	ConnecttoDB() {
		this.setLayout(null);

		labelName.setBounds(20, 10, 60, 30);
		labelPass.setBounds(20, 50, 60, 30);
		tFieldName.setBounds(100, 10, 80, 30);
		pFieldPass.setBounds(100, 50, 80, 30);
		buttonLogin.setBounds(200, 25, 80, 35);

		buttonLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String name = tFieldName.getText();
				String pass = new String(pFieldPass.getPassword());

				Main m = new Main(name, pass);
				dispose();
			}
		});

		this.add(labelName);
		this.add(labelPass);
		this.add(tFieldName);
		this.add(pFieldPass);
		this.add(buttonLogin);

		this.setBounds(140, 140, 350, 130);
		this.setTitle("DB �α���");
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new ConnecttoDB();
	}
}
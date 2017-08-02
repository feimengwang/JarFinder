package cn.true123.jarfinder;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class JarFinder {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private boolean isRunning = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JarFinder window = new JarFinder();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JarFinder() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 763, 367);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		textField = new JTextField();
		textField.setBounds(127, 26, 303, 20);
		textField.setHorizontalAlignment(SwingConstants.LEFT);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		String[] history = History.getHistory();
		if (null != history) {
			textField.setText(history[0] != null ? history[0].substring(4) : "");
		}
		textField_1 = new JTextField();
		textField_1.setBounds(127, 57, 415, 20);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		if (null != history)
			textField_1.setText(history[1] != null ? history[1].substring(7) : "");
		JLabel lblNewLabel = new JLabel("Directory:");
		lblNewLabel.setBounds(44, 26, 98, 20);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Search text:");
		lblNewLabel_1.setBounds(42, 60, 75, 14);
		frame.getContentPane().add(lblNewLabel_1);

		JButton btnSearch = new JButton("Search");

		btnSearch.setBounds(622, 26, 89, 52);
		frame.getContentPane().add(btnSearch);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 88, 724, 233);
		scrollPane.createVerticalScrollBar();
		frame.getContentPane().add(scrollPane);

		final JList<String> list = new JList<String>();
		scrollPane.setViewportView(list);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setBounds(0, 26, 20, 1);
		frame.getContentPane().add(horizontalStrut);

		JButton btnNewButton = new JButton("Select");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				String[] history = History.getHistory();
				if (null != history && history[1] != null) {
					jfc.setCurrentDirectory(new File(history[1]));
				}
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.showDialog(new JLabel(), "Select");
				File file = jfc.getSelectedFile();
				textField.setText(file != null ? file.getAbsolutePath() : textField.getText());
				if (file != null) {
					History.save("dir:" + file.getAbsolutePath());
				}
			}
		});
		btnNewButton.setBounds(453, 25, 89, 23);
		frame.getContentPane().add(btnNewButton);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isRunning)
					return;
				isRunning = true;
				final Vector<String> vector = new Vector<String>();
				vector.addElement("");

				final Timer timer = new Timer(1000, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						vector.set(0, nextString("Searching"));
						list.setListData(vector);

					}
				});
				timer.start();
				History.save("dir:" + textField.getText());
				History.save("search:" + textField_1.getText());
				FindJar findJar = new FindJar();
				findJar.setParam(textField.getText(), textField_1.getText());
				findJar.setCallBack(new CallBack() {
					@Override
					public void callBack(Vector<String> v) {
						timer.stop();
						if (v == null || v.size() > 0) {
							list.setListData(v);
						} else {
							Vector<String> vector = new Vector<String>();
							vector.addElement("Sorry, not found！");
							list.setListData(vector);
						}

					}

					@Override
					public void finish() {
						isRunning = false;

					}
				});
				new Thread(findJar).start();
			}

		});
	}

	private int index;
	private boolean inc = true;

	private String nextString(String text) {
		if (inc)
			index++;
		else
			index--;
		if (index >= 10) {
			inc = false;

		} else if (index <= 0) {
			inc = true;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(text);
		if (!inc)
			sb.append("<-");
		for (int i = 0; i < index; i++) {
			sb.append("---");
		}
		if (inc)
			sb.append("->");

		return sb.toString();
	}

	public interface CallBack {

		public void callBack(Vector<String> v);

		public void finish();
	}
}

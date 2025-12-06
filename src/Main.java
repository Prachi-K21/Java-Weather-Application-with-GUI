import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import org.json.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pixel Weather App");
        frame.setSize(400, 600);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(20, 20, 30)); // dark retro background

        // Load pixel font
        Font pixelFont = new Font("Monospaced", Font.PLAIN, 14); // fallback
        try {
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/PressStart2P.ttf")).deriveFont(14f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Panel for weather info
        JPanel card = new JPanel();
        card.setBounds(30, 100, 330, 450);
        card.setBackground(new Color(30, 30, 50)); // solid pixel color
        card.setLayout(null);
        frame.add(card);

        // Search bar with suggestions
        String[] cityList = {"Mumbai", "Delhi", "Bangalore", "Kolkata", "Chennai", "Pune", "Hyderabad"};
        JComboBox<String> cityField = new JComboBox<>(cityList);
        cityField.setEditable(true);
        cityField.setBounds(40, 40, 220, 35);
        cityField.setFont(pixelFont);
        frame.add(cityField);

        JButton searchBtn = new JButton("SEARCH");
        searchBtn.setBounds(270, 40, 80, 35);
        searchBtn.setFont(pixelFont);
        frame.add(searchBtn);

        // Weather Icon
        JLabel weatherIcon = new JLabel();
        weatherIcon.setBounds(90, 20, 150, 150);
        card.add(weatherIcon);

        // Labels inside card
        JLabel cityLabel = new JLabel("CITY: ");
        cityLabel.setBounds(50, 180, 250, 30);
        cityLabel.setForeground(Color.WHITE);
        cityLabel.setFont(pixelFont);
        card.add(cityLabel);

        JLabel tempLabel = new JLabel("TEMP: ");
        tempLabel.setBounds(50, 220, 250, 30);
        tempLabel.setForeground(Color.WHITE);
        tempLabel.setFont(pixelFont);
        card.add(tempLabel);

        JLabel condLabel = new JLabel("CONDITION: ");
        condLabel.setBounds(50, 260, 250, 30);
        condLabel.setForeground(Color.WHITE);
        condLabel.setFont(pixelFont);
        card.add(condLabel);

        JLabel humidLabel = new JLabel("HUMIDITY: ");
        humidLabel.setBounds(50, 300, 250, 30);
        humidLabel.setForeground(Color.WHITE);
        humidLabel.setFont(pixelFont);
        card.add(humidLabel);

        JLabel windLabel = new JLabel("WIND: ");
        windLabel.setBounds(50, 340, 250, 30);
        windLabel.setForeground(Color.WHITE);
        windLabel.setFont(pixelFont);
        card.add(windLabel);

        // Search button action
        searchBtn.addActionListener(e -> {
            try {
                String city = URLEncoder.encode(((JTextField)cityField.getEditor().getEditorComponent()).getText().trim(), "UTF-8");
                String apiKey = "d63f71258b29dea4a77a0052aa47cf4a"; // your API key

                String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";
                URL url = new URL(urlString);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) response.append(line);
                br.close();

                JSONObject json = new JSONObject(response.toString());

                double temp = json.getJSONObject("main").getDouble("temp");
                String condition = json.getJSONArray("weather").getJSONObject(0).getString("main");
                String name = json.getString("name");
                int humidity = json.getJSONObject("main").getInt("humidity");
                double wind = json.getJSONObject("wind").getDouble("speed");

                cityLabel.setText("CITY: " + name.toUpperCase());
                tempLabel.setText("TEMP: " + (int)temp + "°C");
                condLabel.setText("CONDITION: " + condition.toUpperCase());
                humidLabel.setText("HUMIDITY: " + humidity + "%");
                windLabel.setText("WIND: " + wind + " m/s");

                // Map weather to pixel icons
                String iconPath = "icons/sun.png"; // default
                switch (condition.toLowerCase()) {
                    case "clear": iconPath = "icons/sun.png"; break;
                    case "clouds": iconPath = "icons/cloudy.png"; break;
                    case "rain": iconPath = "icons/rain.png"; break;
                    case "snow": iconPath = "icons/snow.png"; break;
                    case "smoke": iconPath = "icons/smoke.png"; break;
                    case "haze": iconPath = "icons/haze.png"; break;
                }

                // Resize icon to label
                ImageIcon icon = new ImageIcon(iconPath);
                Image img = icon.getImage().getScaledInstance(weatherIcon.getWidth(), weatherIcon.getHeight(), Image.SCALE_SMOOTH);
                weatherIcon.setIcon(new ImageIcon(img));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "City Not Found or API Error!");
            }
        });

        // Auto-complete suggestions
        cityField.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = ((JTextField)cityField.getEditor().getEditorComponent()).getText();
                cityField.removeAllItems();
                for (String c : cityList) {
                    if (c.toLowerCase().startsWith(text.toLowerCase())) {
                        cityField.addItem(c);
                    }
                }
                ((JTextField)cityField.getEditor().getEditorComponent()).setText(text);
                cityField.showPopup();
            }
        });

        frame.setVisible(true);
    }
}

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class RealTimeClock{

    public static void main(String[] args){
        RealTimeClock gui = new RealTimeClock();
        gui.go();
    }
    public void go(){
        JFrame frame = new JFrame("Real Time Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MyPanel drawPanel = new MyPanel();
        frame.getContentPane().add(drawPanel);
        frame.setSize(300,300);

        // add menus
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenu menu1 = new JMenu("Name");
        JMenu menu2 = new JMenu("Student ID");
        menuBar.add(menu1);
        menuBar.add(menu2);
        // add menu items
        JMenuItem item1 = new JMenuItem("WANG JUNJIE");
        JMenuItem item2 = new JMenuItem("1W15BG03-0");
        menu1.add(item1);
        menu2.add(item2);

        frame.setVisible(true);

        // Repaint graphics once a second
        while(true){
            drawPanel.repaint();
            try{
                Thread.sleep(1000);
            }catch(Exception ex){
                System.out.println("Unknown error.");
            }
        }
    }

    class MyPanel extends JPanel{
        public void paintComponent(Graphics g){
            Graphics2D g2d = (Graphics2D)g;
            // De-saw
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Empty the original graphic
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2d.setColor(Color.BLACK);

            // The coordinates of the center of the circle
            int xCenter = this.getWidth() / 2;
            int yCenter = this.getHeight() / 2;
            // calculate the radius of circle
            int radius = (int) (Math.min(this.getWidth(), this.getHeight()) * 0.8 * 0.5);

            // draw circle
            g2d.drawOval(xCenter - radius, yCenter - radius, radius * 2, radius * 2);

            // draw the center point
            paintCenterPoint(g2d, xCenter, yCenter);

            // draw Number 1,2,...,12
            paintNumber(g2d, radius, xCenter, yCenter);

            // draw the scale
            paintDot(g2d, xCenter, yCenter);

            Calendar calendar = Calendar.getInstance();

            // draw 3 pointers: Hour, minute, second
            paintPointer(g2d, calendar, xCenter, yCenter);

            // draw date
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df2 = new SimpleDateFormat("E");
            g2d.drawRoundRect(xCenter-36, yCenter+18, 72, 36, 10, 10);
            g2d.drawString(df1.format(calendar.getTime()), xCenter-30, yCenter+36);
            g2d.drawString(df2.format(calendar.getTime()), xCenter-18, yCenter+50);
        }

        private void paintCenterPoint(Graphics2D g2d, int xCenter, int yCenter){
            g2d.setColor(Color.RED);
            Rectangle2D rect = new Rectangle2D.Double(xCenter-2, yCenter-2, 4, 4);
            g2d.fill(rect);
        }

        private void paintNumber(Graphics2D g2d, int radius, int xCenter, int yCenter){
            g2d.setColor(Color.BLACK);
            for (int i = 0; i < 12; i++)
            {
                // Know the circle center at coordinates (x0, y0), radius r, angle a0
                // then the formula of the coordinates (x, y) of any point on the circle:
                // x = x0 + r * cos(ao * 3.14 /180)
                // y = y0 + r * sin(ao * 3.14 /180)

                double dd = Math.PI / 180 * i * (360 /12); // 360/12 degrees per turn
                int x = (xCenter-4) + (int)((radius - 12) * Math.cos(dd));
                int y = (yCenter+4) + (int)((radius - 12) * Math.sin(dd));

                // Since drawing starts from 3 o'clock, the index i needs to be added 3
                int index = i + 3;
                if (index > 12)
                    index = index - 12;
                g2d.drawString(Integer.toString(index), x, y);
            }
        }

        private void paintDot(Graphics2D g2d, int xCenter, int yCenter){
            for (int i = 0; i < 60; i++)
            {
                int w = i % 5;
                // set number 1,2,...,12's scale to red and bigger
                if (w == 0){
                    w = 5;
                    g2d.setColor(Color.RED);
                }else{
                    w = 3;
                    g2d.setColor(Color.BLUE);
                }
                g2d.fillRect(xCenter-2, 28, w, 3);
                g2d.rotate(Math.toRadians(6), xCenter, yCenter);
            }
        }

        private void paintPointer(Graphics2D g2d, Calendar calendar, int xCenter, int yCenter){

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            // set rotation reset
            AffineTransform old = g2d.getTransform();

            g2d.setColor(Color.BLACK);
            // draw Hour Pointer
            double hAngle = (hour - 12 + minute / 60d) * 360d / 12d;
            g2d.rotate(Math.toRadians(hAngle), xCenter, yCenter);
            int xhArr[] = { xCenter, xCenter+9, xCenter, xCenter-9 };
            int yhArr[] = { 65, yCenter, yCenter+10, yCenter};
            g2d.drawPolygon(xhArr, yhArr, xhArr.length);
            g2d.setTransform(old);

            // draw Minute Pointer
            double mAngle = (minute + second / 60d) * 360d / 60d;
            g2d.rotate(Math.toRadians(mAngle), xCenter, yCenter);
            int xmArr[] = { xCenter, xCenter+6, xCenter, xCenter-6 };
            int ymArr[] = { 45, yCenter, yCenter+12, yCenter};
            g2d.drawPolygon(xmArr, ymArr, xmArr.length);
            g2d.setTransform(old);

            // draw Second Pointer
            double sAngle = second * 360d / 60d;
            g2d.rotate(Math.toRadians(sAngle), xCenter, yCenter);
            g2d.drawLine(xCenter, yCenter+10, xCenter, 35);
            g2d.setTransform(old);
        }
    }
}

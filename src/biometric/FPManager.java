package biometric;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Arrays;
import java.util.HashMap;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Random;


public class FPManager 
{
    static Details dt=new Details();
    
    static ArrayList partial=new ArrayList();
    static ArrayList selPart=new ArrayList();
            
    public static Fingerprint getFingerprint(String url)
    {
        BufferedImage bfImage = null;
        try 
        {
            bfImage = ImageIO.read(new File(url));
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        }
        Fingerprint fingerprint = new Fingerprint(url);
        fingerprint.setWidth(bfImage.getWidth());
        fingerprint.setHeight(bfImage.getHeight());
        fingerprint.setBinaryImage(BasicOperations.applyFilters(bfImage));
        fingerprint.setBufferedImage(bfImage);
        fingerprint.setSkeleton(fingerprint.getBinaryImage());
        return fingerprint;
    }
   
    public static Fingerprint getFingerprint(ImageProducer producer)
    {
        Image image = Toolkit.getDefaultToolkit().createImage(producer);
        BufferedImage bfImage = new BufferedImage(image.getWidth(null)-50, image.getHeight(null)-50,BufferedImage.TYPE_INT_ARGB);      
        Graphics2D g2d = bfImage.createGraphics();
        g2d.drawImage(image,-50,-50,null); //org
        
        g2d.dispose();
       
        Fingerprint fingerprint = new Fingerprint("");
        fingerprint.setWidth(bfImage.getWidth());
        fingerprint.setHeight(bfImage.getHeight());
        fingerprint.setBinaryImage(BasicOperations.applyFilters(bfImage));
        fingerprint.setBufferedImage(bfImage);
        fingerprint.setSkeleton(fingerprint.getBinaryImage());
        return fingerprint;
    }
   
    public static BufferedImage getDirectionalImage(Fingerprint fingerprint)
    {
        Image image = fingerprint.getImage();
        BufferedImage bfImage2 = new BufferedImage(fingerprint.getWidth(), fingerprint.getHeight(),image.SCALE_SMOOTH);
        BufferedImage bfImage = fingerprint.getBufferedImage();
        Graphics2D g2d = bfImage2.createGraphics();
       
        int window = 9;
        int vSquares = (int)Math.floor(fingerprint.getWidth()/window);
        int hSquares = (int)Math.floor(fingerprint.getHeight()/window);
        int n = (int)Math.floor(window/2);
       
        int[][] directions = new int[vSquares-2][hSquares-2];
       
        g2d.drawImage(image,0,0,null);
       
        
        g2d.setColor(Color.red);
        for(int i=1; i<vSquares-1; i++)
        {
            for(int j=1; j<hSquares-1; j++)
            {
                //Pixel central
                Point pixel = new Point((i*window)+n,(j*window)+n);
                int greyLevel = 255-(new Color(bfImage.getRGB(pixel.x, pixel.y))).getRed();
               
                //Outros dados
                int direction = 0;
                int sums = 0;
               
                int[] s = new int[]{0,0,0,0,0,0,0,0};
                 
                for(int w=-n; w<=n/2; w++)
                {
                    s[0] += 255-(new Color(bfImage.getRGB(pixel.x,pixel.y+2*w)).getRed());
                    s[1] += 255-(new Color(bfImage.getRGB(pixel.x+w,pixel.y-2*w)).getRed());
                    s[2] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y-2*w)).getRed());
                    s[3] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y-w)).getRed());
                    s[4] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y)).getRed());
                    s[5] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y+w)).getRed());
                    s[6] += 255-(new Color(bfImage.getRGB(pixel.x+2*w,pixel.y+2*w)).getRed());
                    s[7] += 255-(new Color(bfImage.getRGB(pixel.x+w,pixel.y+2*w)).getRed());
                }
               
                for(int k=0; k<s.length; k++)
                {
                    s[k] = s[k]-greyLevel;
                }
               
                int smin = 0;
                int smax = 0;
               
                for(int w=0; w<s.length; w++)
                {
                    if(s[w]<s[smin])
                    {
                        smin = w;
                    }
                    if(s[w]>s[smax])
                    {
                        smax = w;
                    }
                }
               
                /*
                 Arrays.sort(s);
                 smin = s[0];
                 smax = s[s.length-1];
                 */
               
                if((4*greyLevel+smin+smax)<((3*sums)/s.length))
                {
                    directions[i-1][j-1] = smax;
                }
                else
                {
                    directions[i-1][j-1] = smin;
                }
            }
        }
       
        /*
        int dirWindow = 3;
        int dirVSquares = (int)Math.floor(directions.length/dirWindow);
        int dirHSquares = (int)Math.floor(directions[0].length/dirWindow);
        n = (int)Math.floor(dirWindow/2);
        int[][] newDirections = new int[directions.length][directions[0].length];
       
        for(int i=0; i<dirVSquares; i++){
            for(int j=0; j<dirHSquares; j++){
                int x = (i*dirWindow)+n;
                int y = (j*dirWindow)+n;
               
                Moda moda = new Moda();
               
                moda.adicionar(directions[x-1][y]);
                moda.adicionar(directions[x-1][y+1]);
                moda.adicionar(directions[x][y+1]);
                moda.adicionar(directions[x+1][y+1]);
                moda.adicionar(directions[x+1][y]);
                moda.adicionar(directions[x+1][y-1]);
                moda.adicionar(directions[x][y-1]);
                moda.adicionar(directions[x-1][y-1]);
                moda.adicionar(directions[x][y]);
               
                int newDirection = moda.calcular().intValue();
               
                directions[x-1][y] = newDirection;
                directions[x-1][y+1] = newDirection;
                directions[x][y+1] = newDirection;
                directions[x+1][y+1] = newDirection;
                directions[x+1][y] = newDirection;
                directions[x+1][y-1] = newDirection;
                directions[x][y-1] = newDirection;
                directions[x-1][y-1] = newDirection;
                directions[x][y] = newDirection;
               
            }
        }
        */
        
        for(int i=0; i<directions.length; i++)
        {
            for(int j=0; j<directions[0].length; j++)
            {
                switch(directions[i][j])
                {
                    case 0:
                        g2d.drawLine((i*window),(j*window)+6,(i*window)+10,(j*window)+6);
                        break;
                    case 1:
                        g2d.drawLine((i*window),(j*window)+8,(i*window)+10,(j*window)+4);
                        break;
                    case 2:
                        g2d.drawLine((i*window),(j*window)+10,(i*window)+10,(j*window));
                        break;
                    case 3:
                        g2d.drawLine((i*window)+4,(j*window)+10,(i*window)+8,(j*window));
                        break;
                    case 4:
                        g2d.drawLine((i*window)+6,(j*window)+10,(i*window)+6,(j*window));
                        break;
                    case 5:
                        g2d.drawLine((i*window)+8,(j*window)+10,(i*window)+4,(j*window));
                        break;
                    case 6:
                        g2d.drawLine((i*window)+10,(j*window)+10,(i*window),(j*window));
                        break;
                    case 7:
                        g2d.drawLine((i*window)+10,(j*window)+8,(i*window),(j*window)+4);
                        break;
                }
            }
        }
       
        g2d.dispose();
        return bfImage2;
    }
   
    public static Fingerprint mapMinutiaes(Fingerprint fingerprint)
    {
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte[][] outSkeleton = BasicOperations.copy(fingerprint.getSkeleton());
        int margin = 50; // org
        
        int bif = 0;
        int eol = 0;
       
        Image img = FPManager.toImage(outSkeleton);
        BufferedImage bfImg = new BufferedImage(width, height, Image.SCALE_SMOOTH);
        Graphics2D g2d = bfImg.createGraphics();
        g2d.drawImage(img,0,0,null);
        g2d.dispose();
       
        ArrayList at=new ArrayList();
        for(int i=margin+20; i<width-margin-20; i++)
        {
            for(int j=margin; j<height-margin; j++)
            {
                int patterns = BasicOperations.timesPattern01(i,j,fingerprint.getSkeleton());
                if(fingerprint.getSkeleton()[i][j]==1)
                {
                    if(patterns==1)
                    {
                        outSkeleton = drawRectangle(i,j,outSkeleton,2);
                        eol++;
                        int theta=findTheta(outSkeleton,i,j,1);
                        //System.out.println(i+" : "+j+" : "+theta);
                        at.add(i+"#"+j+"#"+theta);
                    }
                    if(patterns==3)
                    {
                        outSkeleton = drawRectangle(i,j,outSkeleton,3);
                        bif++;
                        int theta=findTheta(outSkeleton,i,j,3);
                        at.add(i+"#"+j+"#"+theta);
                       // System.out.println(i+" : "+j+" : "+theta);
                    }
                }
            }
        }
         
        dt.features=at;
        //System.out.println("================");
        
        fingerprint.setSkeleton(outSkeleton);
        fingerprint.setBifurcations(bif);
        fingerprint.setEndoflines(eol);
        return fingerprint;
    }
   
    public static Fingerprint reduceMinutiaes(Fingerprint fingerprint)
    {
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte[][] outSkeleton = BasicOperations.copy(fingerprint.getSkeleton());
        int margin = 50;
        int bif = 0;
        int eol = 0;
       
        Image img = FPManager.toImage(outSkeleton);
        BufferedImage bfImg = new BufferedImage(width, height, Image.SCALE_SMOOTH);
        Graphics2D g2d = bfImg.createGraphics();
        g2d.drawImage(img,0,0,null);
        g2d.dispose();
       
        ArrayList at=new ArrayList();
        for(int i=margin+20; i<width-margin-20; i++)
        {
            for(int j=margin; j<height-margin; j++)
            {
                int patterns = BasicOperations.timesPattern01(i,j,fingerprint.getSkeleton());
                if(fingerprint.getSkeleton()[i][j]==1)
                {
                    if(patterns==1)
                    {
                        //outSkeleton = drawRectangle(i,j,outSkeleton,2);
                        eol++;
                        int theta=findTheta(outSkeleton,i,j,1);
                        //System.out.println(i+" : "+j+" : "+theta);
                        String g1=i+"#"+j+"#"+theta;
                        if(dt.selFeat.contains(g1))
                            outSkeleton = drawRectangle(i,j,outSkeleton,2);
                    }
                    if(patterns==3)
                    {
                        //outSkeleton = drawRectangle(i,j,outSkeleton,3);
                        bif++;
                        int theta=findTheta(outSkeleton,i,j,3);
                        String g1=i+"#"+j+"#"+theta;
                        if(dt.selFeat.contains(g1))
                            outSkeleton = drawRectangle(i,j,outSkeleton,3);
                       // System.out.println(i+" : "+j+" : "+theta);
                    }
                }
            }
        }
         
        
        //System.out.println("================");
        
        fingerprint.setSkeleton(outSkeleton);
        fingerprint.setBifurcations(bif);
        fingerprint.setEndoflines(eol);
        return fingerprint;
    }
   
    
    
    public static Fingerprint addRandomMinutiaes(Fingerprint fingerprint,ArrayList sel)
    {
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte[][] outSkeleton = BasicOperations.copy(fingerprint.getSkeleton());
        int margin = 50;
        int bif = 0;
        int eol = 0;
       
        dt.random1=new ArrayList();
        
        Image img = FPManager.toImage(outSkeleton);
        BufferedImage bfImg = new BufferedImage(width, height, Image.SCALE_SMOOTH);
        Graphics2D g2d = bfImg.createGraphics();
        g2d.drawImage(img,0,0,null);
        g2d.dispose();
       
        
        Random rn=new Random();
        for(int i=0;i<100;i++)
        {
            int a1=rn.nextInt(width-margin-20);
            if(a1<margin+20)
                a1=a1+margin+20;
            int b1=rn.nextInt(height-margin);
            if(b1<margin)
                b1=b1+margin;
            
            int theta=findTheta(outSkeleton,a1,b1,1);
             
            String g1=a1+"#"+b1+"#"+theta;
            if(sel.contains(g1))
            {
                if(!dt.random1.contains(g1))
                {
                    dt.random1.add(g1);
                    outSkeleton = drawRectangle(a1,b1,outSkeleton,2);
                }
            }
            
        }
        /*for(int i=margin+20; i<width-margin-20; i++)
        {
            for(int j=margin; j<height-margin; j++)
            {
                int patterns = BasicOperations.timesPattern01(i,j,fingerprint.getSkeleton());
                if(fingerprint.getSkeleton()[i][j]==1)
                {
                    if(patterns==1)
                    {
                        //outSkeleton = drawRectangle(i,j,outSkeleton,2);
                        eol++;
                        int theta=findTheta(outSkeleton,i,j,1);
                        //System.out.println(i+" : "+j+" : "+theta);
                        String g1=i+"#"+j+"#"+theta;
                        if(dt.selFeat.contains(g1))
                            outSkeleton = drawRectangle(i,j,outSkeleton,2);
                    }
                    if(patterns==3)
                    {
                        //outSkeleton = drawRectangle(i,j,outSkeleton,3);
                        bif++;
                        int theta=findTheta(outSkeleton,i,j,3);
                        String g1=i+"#"+j+"#"+theta;
                        if(dt.selFeat.contains(g1))
                            outSkeleton = drawRectangle(i,j,outSkeleton,3);
                       // System.out.println(i+" : "+j+" : "+theta);
                    }
                }
            }
        }
         */
        
        //System.out.println("================");
        
        fingerprint.setSkeleton(outSkeleton);
        fingerprint.setBifurcations(bif);
        fingerprint.setEndoflines(eol);
        return fingerprint;
    }
   
    
    
    ///// 
    public static Fingerprint PartmapMinutiaes(Fingerprint fingerprint)
    {
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte[][] outSkeleton = BasicOperations.copy(fingerprint.getSkeleton());
        int margin = 50; // org
        
        int bif = 0;
        int eol = 0;
       
        Image img = FPManager.toImage(outSkeleton);
        BufferedImage bfImg = new BufferedImage(width, height, Image.SCALE_SMOOTH);
        Graphics2D g2d = bfImg.createGraphics();
        g2d.drawImage(img,0,0,null);
        g2d.dispose();
       
        partial=new ArrayList();
        for(int i=margin+20; i<width-margin-20; i++)
        {
            for(int j=margin; j<height-margin; j++)
            {
                int patterns = BasicOperations.timesPattern01(i,j,fingerprint.getSkeleton());
                if(fingerprint.getSkeleton()[i][j]==1)
                {
                    if(patterns==1)
                    {
                        outSkeleton = drawRectangle(i,j,outSkeleton,2);
                        eol++;
                        int theta=findTheta(outSkeleton,i,j,1);
                        //System.out.println(i+" : "+j+" : "+theta);
                        partial.add(i+"#"+j+"#"+theta);
                    }
                    if(patterns==3)
                    {
                        outSkeleton = drawRectangle(i,j,outSkeleton,3);
                        bif++;
                        int theta=findTheta(outSkeleton,i,j,3);
                        partial.add(i+"#"+j+"#"+theta);
                       // System.out.println(i+" : "+j+" : "+theta);
                    }
                }
            }
        }
         
        //dt.features=at;
        //System.out.println("================");
        
        fingerprint.setSkeleton(outSkeleton);
        fingerprint.setBifurcations(bif);
        fingerprint.setEndoflines(eol);
        return fingerprint;
    }
   
    public static Fingerprint PartreduceMinutiaes(Fingerprint fingerprint,ArrayList sel1)
    {
        int width = fingerprint.getWidth();
        int height = fingerprint.getHeight();
        byte[][] outSkeleton = BasicOperations.copy(fingerprint.getSkeleton());
        int margin = 50;
        int bif = 0;
        int eol = 0;
       
        Image img = FPManager.toImage(outSkeleton);
        BufferedImage bfImg = new BufferedImage(width, height, Image.SCALE_SMOOTH);
        Graphics2D g2d = bfImg.createGraphics();
        g2d.drawImage(img,0,0,null);
        g2d.dispose();
       
        selPart=sel1;
        
        for(int i=margin+20; i<width-margin-20; i++)
        {
            for(int j=margin; j<height-margin; j++)
            {
                int patterns = BasicOperations.timesPattern01(i,j,fingerprint.getSkeleton());
                if(fingerprint.getSkeleton()[i][j]==1)
                {
                    if(patterns==1)
                    {
                        //outSkeleton = drawRectangle(i,j,outSkeleton,2);
                        eol++;
                        int theta=findTheta(outSkeleton,i,j,1);
                        //System.out.println(i+" : "+j+" : "+theta);
                        String g1=i+"#"+j+"#"+theta;
                        if(selPart.contains(g1))
                            outSkeleton = drawRectangle(i,j,outSkeleton,2);
                    }
                    if(patterns==3)
                    {
                        //outSkeleton = drawRectangle(i,j,outSkeleton,3);
                        bif++;
                        int theta=findTheta(outSkeleton,i,j,3);
                        String g1=i+"#"+j+"#"+theta;
                        if(selPart.contains(g1))
                            outSkeleton = drawRectangle(i,j,outSkeleton,3);
                       // System.out.println(i+" : "+j+" : "+theta);
                    }
                }
            }
        }
         
        
        //System.out.println("================");
        
        fingerprint.setSkeleton(outSkeleton);
        fingerprint.setBifurcations(bif);
        fingerprint.setEndoflines(eol);
        return fingerprint;
    }
    
    
    
    
    
    private  static byte[][] drawRectangle(int x, int y, byte[][] skeleton, int color)
    {
        int size = 3;
        for(int i=-size; i<=size; i++)
        {
            skeleton[x-i][y+size] = (byte)color;
            skeleton[x+i][y-size] = (byte)color;
            skeleton[x-size][y+i] = (byte)color;
            skeleton[x+size][y-i] = (byte)color;
        }
        return skeleton;
    }
   
    public  static Image toImage(byte[][] image)
    {
        int width = image.length;
        int height = image[0].length;
        BufferedImage bfImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        for(int i=0; i<width; i++)
        {
            for(int j=0; j<height; j++)
            {
                if(image[i][j]==0)
                {
                    bfImage.setRGB(i,j,Color.white.getRGB());
                }
                if(image[i][j]==1)
                {
                    bfImage.setRGB(i,j,Color.black.getRGB());
                }
                if(image[i][j]==2)
                {
                    bfImage.setRGB(i,j,Color.blue.getRGB());
                }
                if(image[i][j]==3)
                {
                    bfImage.setRGB(i,j,Color.red.getRGB());
                }
                if(image[i][j]==4)
                {
                    bfImage.setRGB(i,j,Color.green.getRGB());
                }
            }
        }
        return bfImage.getScaledInstance(width, height,0);
    }
    
    public static int findTheta(byte[][] img,int x,int y,int cn)
    {
        int theta = -1;
        try
        {
            if (cn == 1)
            {
                for(int i=0;i<8;i++)
                {
                    if (findP(img, x, y, i))
                    {
                        switch (i)
                        {
                            case 1: case 9: 
                                theta = 0; break;
                            case 2:
                                theta = 45; break;
                            case 3:
                                theta = 90;break;
                            case 4:
                                theta = 135;break;
                            case 5:
                                theta = 180;break;
                            case 6:
                                theta = 225;break;
                            case 7:
                                theta = 270;break;
                            case 8:
                                theta = 315;break;
                        }
                    }
                }
            }
            else
            {
                if (findP(img, x, y, 1)  && !(findP(img, x, y, 2) && findP(img, x, y, 8)) )
                    theta = 0;
    
                if (findP(img, x, y, 2) && !(findP(img, x, y, 1) && findP(img, x, y, 8) && findP(img, x, y, 3) && findP(img, x, y, 4)))
                    theta = 45;
    
                if (findP(img, x, y, 3)&& !(findP(img, x, y, 2) && findP(img, x, y, 4)))
                    theta = 90;
    
                if (findP(img, x, y, 4)&& ! (findP(img, x, y, 3) && findP(img, x, y, 2) && findP(img, x, y, 5) && findP(img, x, y, 6)))
                    theta = 135;
                if (findP(img, x, y, 5) && ! (findP(img, x, y, 4) && findP(img, x, y, 6)))
                    theta = 180;
                if (findP(img, x, y, 6) && !(findP(img, x, y, 4) && findP(img, x, y, 5) && findP(img, x, y, 7) && findP(img, x, y, 8)))
                    theta = 225;
    
                if (findP(img, x, y, 7) && ! (findP(img, x, y, 6) && findP(img, x, y, 8)))
                    theta = 270;
    
                if (findP(img, x, y, 8) && ! (findP(img, x, y, 2) && findP(img, x, y, 1) && findP(img, x, y, 7) && findP(img, x, y, 6)))
                    theta = 315;
    
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return theta;
    }
   
    public  static boolean findP(byte[][] img,int x,int y,int cn)
    {
        int j=0;
        switch (cn)
        {
            case 1: case 9:
                j = img[x+1][y];
                break;
            case 2:
                j = img[x + 1][y-1];
                break;
            case 3:
                j = img[x][y - 1];
                break;
            case 4:
                j = img[x - 1][y - 1];
                break;
            case 5:
                j = img[x - 1][y];
                break;
            case 6:
                j = img[x - 1][y + 1];
                break;
            case 7:
                j = img[x][y + 1];
                break;
            case 8:
                j = img[x + 1][y + 1];
                break;
        }
        if(j==1)
            return true;
        else
            return false;
    }
}

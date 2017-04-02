/* 
 * Copyright (C) 2017 Tanesh Manjrekar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cryptogen;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author Tanesh Manjrekar
 */
public class CryptoFrame extends JFrame implements ActionListener
{
    private JPanel outputPanel, 
            inputTextPanel,
            inputTextScrollPanePanel, 
            inputImagePanel;
    private JTabbedPane inputTabbedPane;
    private JScrollPane inputTextScrollPane;
    private JTextField outputDirectoryTextField, 
            outputFileTextField, 
            inputImageTextField;
    private JTextArea inputTextTextArea;
    private JSpinner numberOfImagesSpinner,
            inputTextWidthSpinner,
            inputTextHeightSpinner,
            inputTextFontSizeSpinner;
    private JLabel outputDirectoryLabel, 
            filePrefixLabel,
            numberOfImagesLabel,
            inputTextLabel, 
            inputTextWidthLabel,
            inputTextHeightLabel,
            inputTextSizeLabel,
            inputImageLabel;
    private JButton outputDirectoryBrowseButton, 
            validateOutputDetailsButton,
            encryptTextButton,
            previewTextImageButton,
            inputImageBrowseButton,
            encryptImageButton;
    
    private boolean outputIsEditable = true;
    private final static int MAX_IMAGE_HEIGHT = 900;
    private final static int MAX_IMAGE_WIDTH = 1600;
    public final static Dimension MAX_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    
    private CryptoFrame()
    {
        //Initialising frame with layout
        super();
        setLayout(new MigLayout("fill"));
        
        createOutputPanel();
        createInputTabbedPane();
        
        //Setting frame constraints
        setTitle("CryptoGen");
        setSize(600, 400);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public static void createFrame()
    {
        CryptoFrame cryptoFrame;
        cryptoFrame = new CryptoFrame();
    }
    
    private void createOutputPanel()
    {
        outputPanel = new JPanel(new MigLayout("fillx, gapy 10", "[][grow][][]"));
        outputDirectoryTextField = new JTextField();
        outputFileTextField = new JTextField();
        numberOfImagesSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 10, 1));
        numberOfImagesLabel = new JLabel("Number of images:");
        outputDirectoryLabel = new JLabel("Output directory:");
        filePrefixLabel = new JLabel("File prefix:");
        outputDirectoryBrowseButton = new JButton("Browse");
        outputDirectoryBrowseButton.addActionListener(this);
        validateOutputDetailsButton = new JButton("Validate");
        validateOutputDetailsButton.addActionListener(this);
        
        outputPanel.add(filePrefixLabel);
        outputPanel.add(outputFileTextField, "growx, span 2, gapx 0 15");
        outputPanel.add(numberOfImagesLabel, "split 2");
        outputPanel.add(numberOfImagesSpinner, "wrap, width 30:35:40");
        outputPanel.add(outputDirectoryLabel);
        outputPanel.add(outputDirectoryTextField, "growx, split 3, span 3");
        outputPanel.add(outputDirectoryBrowseButton, "align c, wrap");
        outputPanel.add(validateOutputDetailsButton, "span 4, align c, width 150:200:250");
        add(outputPanel, "grow, wrap");
    }
    
    private void createInputTabbedPane()
    {
        inputTabbedPane = new JTabbedPane();
        
        createInputTextPanel();
        createInputImagePanel();
        
        add(inputTabbedPane, "grow");
        inputTabbedPane.setVisible(false);
    }
    
    private void createInputTextPanel()
    {
        inputTextPanel = new JPanel(new MigLayout("fillx", "[grow][][]"));
        inputTextScrollPanePanel = new JPanel(new MigLayout("fill, insets 0"));
        inputTextLabel = new JLabel("Enter text:");
        inputTextWidthLabel = new JLabel("Image width:");
        inputTextHeightLabel = new JLabel("Image height:");
        inputTextSizeLabel = new JLabel("Font size:");
        inputTextTextArea = new JTextArea();
        inputTextTextArea.setLineWrap(true);
        inputTextTextArea.setWrapStyleWord(true);
        inputTextScrollPane = new JScrollPane(inputTextTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputTextWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, MAX_IMAGE_WIDTH, 100));
        inputTextHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, MAX_IMAGE_HEIGHT, 100));
        inputTextFontSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 400, 10));
        encryptTextButton = new JButton("Encrypt text");
        encryptTextButton.addActionListener(this);
        previewTextImageButton = new JButton("Preview image");
        previewTextImageButton.addActionListener(this);
        
        inputTextScrollPanePanel.add(inputTextScrollPane, "grow");
        
        inputTextPanel.add(inputTextLabel, "wrap");
        inputTextPanel.add(inputTextScrollPanePanel, "grow, span 1 4, wrap");
        inputTextPanel.add(inputTextWidthLabel);
        inputTextPanel.add(inputTextWidthSpinner, "wrap, grow");
        inputTextPanel.add(inputTextHeightLabel);
        inputTextPanel.add(inputTextHeightSpinner, "wrap, grow");
        inputTextPanel.add(inputTextSizeLabel);
        inputTextPanel.add(inputTextFontSizeSpinner, "wrap, growx");
        inputTextPanel.add(previewTextImageButton, "span 3, split 2, align c, width 150:200:250, gaptop 15, gapright 40");
        inputTextPanel.add(encryptTextButton, "align c, width 150:200:250");
        
        inputTabbedPane.addTab("Text", inputTextPanel);
    }
    
    private void createInputImagePanel()
    {
        inputImagePanel = new JPanel(new MigLayout("fillx", "[grow][]"));
        inputImageLabel = new JLabel("Choose image:");
        inputImageTextField = new JTextField();
        inputImageBrowseButton = new JButton("Browse");
        inputImageBrowseButton.addActionListener(this);
        encryptImageButton = new JButton("Encrypt image");
        encryptImageButton.addActionListener(this);
        
        inputImagePanel.add(inputImageLabel, "wrap");
        inputImagePanel.add(inputImageTextField, "growx");
        inputImagePanel.add(inputImageBrowseButton, "wrap");
        inputImagePanel.add(encryptImageButton, "gaptop 15, span 2, align c, width 150:200:250");
        
        inputTabbedPane.addTab("Image", inputImagePanel);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource().equals(outputDirectoryBrowseButton))
        {
            chooseDirectory();
        }
        
        else if(ae.getSource().equals(validateOutputDetailsButton))
        {
            if(outputIsEditable)
            {
                validateOutputDetails();
            }
            else
            {
                makeOutputEditable();
            }
        }
        else if(ae.getSource()==previewTextImageButton)
        {
            previewText();
        }
        else if(ae.getSource()==encryptTextButton)
        {
            encryptText();
        }
        else if(ae.getSource()==inputImageBrowseButton)
        {
            chooseImage();
        }
        else if(ae.getSource()==encryptImageButton)
        {
            encryptImage();
        }
    }
    
    private void chooseDirectory()
    {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setDialogTitle("Choose output directory");
        if(directoryChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
        {
            outputDirectoryTextField.setText(directoryChooser.getSelectedFile().getPath());
        }
    }
    
    private void validateOutputDetails()
    {
        File outputDirectory = new File(outputDirectoryTextField.getText());
        
        //Checking if the selected directory exists
        if(outputDirectory.exists() && outputDirectory.isDirectory())
        {
            //Checking if file prefix hasn't been specified
            if(outputFileTextField.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Enter a file prefix!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                File outputFile = new File(outputDirectoryTextField.getText() + "\\" + outputFileTextField.getText() + ".png");
                //Checking if output file already exists
                boolean fileExists = false;
                for(int i = 1; i<11; i++)
                {
                     if(new File(outputDirectoryTextField.getText() + "\\" + outputFileTextField.getText() + " - " + i +".png").exists())
                     {
                         fileExists = true;
                     }
                }
                if(fileExists)
                {
                    JOptionPane.showMessageDialog(this, "File already exists!", "Error!", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    /*Checking if file prefix is valid by creating a dummy file by that name
                    If creation is successful, the dummy file is deleted*/
                    try
                    {
                        boolean validFileName = outputFile.createNewFile();
                        if(validFileName)
                        {
                            outputFile.delete();
                            displayInput();
                        }
                    }
                    catch(IOException ioe)
                    {
                        System.out.println(ioe);
                        JOptionPane.showMessageDialog(this, "Invalid file prefix!", "Error!", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Invalid directory!", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void makeOutputEditable()
    {
        outputFileTextField.setEnabled(true);
        outputDirectoryTextField.setEnabled(true);
        outputDirectoryBrowseButton.setEnabled(true);
        numberOfImagesSpinner.setEnabled(true);
        validateOutputDetailsButton.setText("Validate");
        outputIsEditable = true;
        inputTabbedPane.setVisible(false);
    }
    
    private void displayInput()
    {
        outputFileTextField.setEnabled(false);
        outputDirectoryTextField.setEnabled(false);
        outputDirectoryBrowseButton.setEnabled(false);
        numberOfImagesSpinner.setEnabled(false);
        validateOutputDetailsButton.setText("Edit output");
        outputIsEditable = false;
        inputTabbedPane.setVisible(true);
    }
    
    private void previewText()
    {
        BufferedImage preview = Encrypter.generateImage(inputTextTextArea.getText(), (Integer)inputTextFontSizeSpinner.getValue(), (Integer)inputTextHeightSpinner.getValue(), (Integer)inputTextWidthSpinner.getValue());
        JFrame previewFrame = new JFrame();
        JLabel previewLabel = new JLabel(new ImageIcon(preview));
        JScrollPane previewScrollPane = new JScrollPane(previewLabel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        previewFrame.add(previewScrollPane);
        previewFrame.setSize(MAX_SIZE);
        previewFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        previewFrame.setLocationRelativeTo(null);
        previewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        previewFrame.setVisible(true);
    }
    
    private void encryptText()
    {
        BufferedImage image = Encrypter.generateImage(inputTextTextArea.getText(), (Integer)inputTextFontSizeSpinner.getValue(), (Integer)inputTextHeightSpinner.getValue(), (Integer)inputTextWidthSpinner.getValue());
        if(Encrypter.encryptImage(image, (Integer) numberOfImagesSpinner.getValue(), new File(outputDirectoryTextField.getText())))
        {
            JOptionPane.showMessageDialog(this, "Success!");
        }
    }
    
    private void chooseImage()
    {
        JFileChooser imageChooser = new JFileChooser();
        imageChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        imageChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        imageChooser.setDialogTitle("Choose input image");
        if(imageChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
        {
            inputImageTextField.setText(imageChooser.getSelectedFile().getPath());
        }
    }
    
    private void encryptImage()
    {
        File imagePath = new File(inputImageTextField.getText());
        if(imagePath.exists())
        {
            try
            {
                BufferedImage image = ImageIO.read(imagePath);
                if(Encrypter.encryptImage(image, (Integer) numberOfImagesSpinner.getValue(), new File(outputDirectoryTextField.getText())))
                {
                    JOptionPane.showMessageDialog(this, "Success!");
                }
            }
            catch(IOException ioe)
            {
                System.out.println(ioe);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Invalid file!", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }
}

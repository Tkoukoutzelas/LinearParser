import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class Parser {

	private ArrayList<String> minmax= new ArrayList<String>();
	private ArrayList<ArrayList<Double>> A= new ArrayList<ArrayList<Double>>();
	private ArrayList<Double> c= new ArrayList<Double>();
	private ArrayList<Integer> Eqin= new ArrayList<Integer>();
	private ArrayList<Double> b= new ArrayList<Double>();
	private int stlinecount=0;
	private BufferedWriter exit;
	private int objxcount=0;
	
	int MinMax;
	public Parser(){
		ArrayList<String> lines = new ArrayList<String>();
		int lineCount=0;
		
		
		minmax.add("min"); minmax.add("max");
		
		//Option to select a certain file from the system
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		
		
		File f = fc.getSelectedFile();
		
		
		//Reading the file from the system line to line and saving each line to an ArrayList
		try
		{
		 BufferedReader reader = new BufferedReader( new FileReader(f) );
		 String line = reader.readLine();
		 while(line != null)
		 {
			 if(line.isBlank()==false) {
		       lines.add(line);
		       lineCount++;}
		     line = reader.readLine();
		 }
		 reader.close();
		}
		catch(FileNotFoundException e)
		{
		   e.printStackTrace();
		}
		catch(IOException e)
		{
		 e.printStackTrace();
		}
		
		
		//Deciding actions to be done, depending on each line
		for(int i=0;i<lineCount;i++) {
			
				if(lines.get(i).replaceAll("\\s", "").equals("end"))
					break;
				if(i+1==lineCount &&!(lines.get(i).replaceAll("\\s", "").equals("end"))){
					System.out.println("Keyword end not found please your correct linear problem form");
					System.exit(1);
				}
			
			
			ParsingProcess(lines.get(i),i);
			
			}
	
		//Outputting the necessary data in a new file inside the project
		File output = new File("Output/Converted.txt");
		try {
			exit =  new BufferedWriter(new FileWriter(output));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			exit.write("MinMax = " + MinMax + "\n\n");
			exit.write("c = " + c + "\n\n");
			exit.write("A = [" );
			int cout = 0;
			for(ArrayList<Double> i:A){
				if(cout == 0)
					exit.write(i.toString() + "\n");
				else
					exit.write("    " + i.toString()+ "\n");
				cout++;
			}
			exit.write("        ]\nb = " + b + "\n\n");
			exit.write("Eqin = " + Eqin);
			exit.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	//Parsing actions for each line
	public void ParsingProcess(String line,int index) {
		
		if(index==0) {
			checkForMinMax(line);
			checkforMissingSigns(line,index);
			getFactors(line,index);
			
			
		}
		else if(index==1) {
			checkforSt(line);
			if(!(stlinecount==1)) {
				checkforMissingSigns(line,index);
				getFactors(line,index);
				getEqin(line);
				getRightSide(line,index);
			}
			
		}
		else if(index>1) {
			checkforMissingSigns(line,index);
			getFactors(line,index);
			getEqin(line);
			getRightSide(line,index);
		}
		
		
	}
	
	
	//Checking the type of linear problem
	public void checkForMinMax(String line) {
		
		Scanner in= new Scanner(line);
		
		   String lptype=in.next();
		   if(minmax.contains(lptype)) {
			   if(lptype=="min") {
				   MinMax=-1;
			   }	
			   else 
				   {MinMax=1;}   
		   		}
		   else {
			   System.out.println("Wrong form in Linear Problem,cannot parse");
			   System.exit(1);
		   	}
		   
		   in.close();
		   
	}
	
	
	
	//Getting the factors of each variable in the technological constraints
	public void getFactors(String line,int index) {
		ArrayList<Integer> xIndexes =new ArrayList<Integer>();//Indexes of variables
		ArrayList<Double> F= new ArrayList<Double>();//Factors array
		ArrayList<Integer> vartrace=new ArrayList<Integer>();//Tracing which variables are not multiplied with zero
		String temp;
		int tempn;//Variable number
		for(int i=0;i<objxcount;i++) {
			F.add(0.0);
		}
		
		
		if(index==0) {
		
			temp=line.substring(6,line.length());
		}
		else if(index==1) {
			if(line.contains("st")) {
			temp=line.substring(3,line.length());}
			else if(line.contains("s.t.")) {
				temp=line.substring(4,line.length());
			}
			else {
				temp=line.substring(9,line.length());
			}
		}
		else {
			temp=line;
		}
		
		temp=temp.replaceAll("\\s","");
		
		for(int i=0;i<temp.length();i++) {
			if(temp.charAt(i)=='x') {
				xIndexes.add(i);
				tempn=Character.getNumericValue(temp.charAt(i+1))-1;
				vartrace.add(tempn);
				}
		}
		
				
		for(int i=0;i<xIndexes.size();i++) {
			
			if(i==0) {
					if((temp.substring(0,xIndexes.get(0))).equals("")){
						F.set(vartrace.get(i),1.0);
					
					}
						else if( (temp.substring(0,xIndexes.get(0))).equals("-")) {
							F.set(vartrace.get(i),-1.0);
						}
						else {
							F.set(vartrace.get(i),Double.parseDouble(temp.substring(0,xIndexes.get(0))));}
					
			}
			
			else {
				
				if((temp.substring(xIndexes.get(i-1)+2,xIndexes.get(i))).equals("+")) {
					F.set(vartrace.get(i),1.0);
					}
				else if((temp.substring(xIndexes.get(i-1)+2,xIndexes.get(i))).equals("-")) {
					F.set(vartrace.get(i),-1.0);
				}
				else {
				F.set(vartrace.get(i),Double.parseDouble(temp.substring(xIndexes.get(i-1)+2,xIndexes.get(i))));}
			       
			  
			   
			    }
			
			}
		
		
		if (index==0) {
			c= new ArrayList<Double>(F);
			}
		else if(index==1) {
			A.add(new ArrayList<Double>());
			A.get(index-1).addAll(F);
			
			
		}
		else if(index>1) {
			A.add(new ArrayList<Double>(F));
		}
		
	}
	
	
	//Checking for keyword "Subject to"
	public void checkforSt(String line) {
		
		if(!(line.toLowerCase().contains("s.t")) &&!(line.toLowerCase().contains("subject to")) &&!(line.toLowerCase().contains("st"))) {
			System.out.println("Please make sure you have entered the right keyword before constraints");
			System.exit(1);
		}
		if(line.toLowerCase().trim().equals("s.t") ||line.toLowerCase().trim().equals("subject to") ||line.toLowerCase().trim().equals("st")) {
			stlinecount=1;
		}
		
	}
	
	
	//Checking if signs such as "+" and "-" are missing from the functions
	public void checkforMissingSigns(String line,int index) {
		
		int varcounter=0;
		int signcounter=0;
		
		
		for(int i=0;i<line.length();i++) {
			if(line.charAt(i)=='x') {
				varcounter++;
				if(index==0) {
					objxcount++;
					}
			}
			if(line.charAt(i)=='+' || line.charAt(i)=='-') {
				signcounter++;
			}
				
		}
		
		if(index==0 && MinMax==1) {
			varcounter--;
			objxcount--;
		}
		
		if(index>0) {
			if(varcounter>objxcount) {
				System.out.println("The variables in your technological constraint are overnumbered,please check for mistakes");
				System.exit(1);
			}
		}
		
		if(!(varcounter-1==signcounter) &&!(varcounter==signcounter)) {
			
			System.out.println("Sign(s) missing please check your function or constraint");
			System.exit(1);
		}
		
		
		
		
	}
	
	
	
	//Getting the type of constraint
	public void getEqin(String line) {
		
		if(line.contains("<=")) {
			Eqin.add(-1);
			
		}
		else if(line.contains("=>")) {
			Eqin.add(1);
			
		}
		else if(line.contains("=")) {
			Eqin.add(0);
		}
		else {
			System.out.println("Please type the correct form of the restricion you want");
			System.exit(1);
		}
		
		
		
		
		
	}
	
	//Getting the right side number from each constraint
	public void getRightSide(String line,int index) {
		
		int start=0;
		line=line.replaceAll("\\s","");
		if(Eqin.get(Eqin.size()-1)==-1 || Eqin.get(Eqin.size()-1)==0) {
			start=line.indexOf('=');
			
		}
		else if(Eqin.get(Eqin.size()-1)==1) {
			start=line.indexOf('>');
		}
		
		String tmp=line.substring(start+1,line.length());
		try {
			b.add(Double.parseDouble(tmp));
		}
		catch(NumberFormatException e) {
			System.out.println("Constraint does not contain right side, please correct");
			System.exit(1);
		}
		
	}
	
	
	
	
}

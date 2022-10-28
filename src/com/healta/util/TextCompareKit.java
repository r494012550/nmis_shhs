package com.healta.util;

import com.jfinal.kit.StrKit;

public class TextCompareKit {
	
	public static String[] compare(String basestr,String str) {
		String[] ret=new String[2];
		if(StrKit.isBlank(basestr)||StrKit.isBlank(str)) {
			ret[0]=basestr;
			ret[1]=str;
			return ret;
		}
		else {
			return trace(ld(basestr,str), basestr, str);
		}
	}
	
	private static int min(int one, int two, int three) {
        int min = one;
        if (two < min) {
            min = two;
        }
        if (three < min) {
            min = three;
        }
        return min;
    }
 
    public static int[][] ld(String str1, String str2) {
        int d[][]; // 矩阵
        int n = str1.length();
        int m = str2.length();
        int i; // 遍历str1的
        int j; // 遍历str2的
        char ch1; // str1的
        char ch2; // str2的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
//        if (n == 0) {
//            return m;
//        }
//        if (m == 0) {
//            return n;
//        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }
        for (i = 1; i <= n; i++) { // 遍历str1
            ch1 = str1.charAt(i - 1);
            // 去匹配str2
            for (j = 1; j <= m; j++) {
                ch2 = str2.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]+ temp);
            }
        }
        
//        for (i = 0; i <= n; i++) { // 遍历str1
//           
//            for (j = 0; j <= m; j++) {
//               System.out.print(d[i][j]);
//            }
//            
//            System.out.println("");
//        }

        
        return d;
    }
    
    public static String[] trace(int d[][],String str1,String str2) {
    	int r = str1.length();
        int c = str2.length();
        /*System.out.println("c="+c);
        System.out.println("r="+r);*/
        
        String A="";
        String B="";
        
        while(c>0&&r>0&&!(r==0&&c==0)) {
        	char a =(" "+str1).charAt(r);
        	char b =(" "+str2).charAt(c);
        	/*System.out.println("a="+a);
            System.out.println("b="+b);
        	System.out.println("c="+c);
            System.out.println("r="+r);*/
        	if(a==b) {
        		if(r==1&&c==1) {
        			r=r-1;
        			c=c-1;
        			A=a+A;
    				B=b+B;
        		}else {
	        		if(r==1) {//第一行  左边
	        			if(c>0) {
	        				c=c-1;
	        				//A="_"+A;
	        				B=b+B;
	        			}
	        		}else if(c==1) {//第一列  上边
	        			if(r>0) {
	        				r=r-1;
	        				
	        				A=a+A;
	        				//B="_"+B;
	        			}
	        		}else {//左上
	        			r=r-1;
	        			c=c-1;
	        			
	        			A=a+A;
	    				B=b+B;
	        		}
        		}
        	}
        	else {
        		if(r==1&&c==1) {
        			A=a+A;
    				B=modifyStyle(b)+B;
    				r=r-1;
        			c=c-1;
        		}else {
	        		if(r==1) {//第一行  左边
	        			if(c>0) {
	        				c=c-1;
	        				//A="_"+A;
	        				B=b+B;
	        			}
	        		}else if(c==1) {//第一列  上边
	        			if(r>0) {
	        				r=r-1;
	        				A=a+A;
	        				//B="_"+B;
	        			}
	        		}else {//左上 上边 左边 中最小
	        			int mi=min1(d[r-1][c-1],d[r-1][c],d[r][c-1]);
	        			if(mi==1) {
	        				r=r-1;
	            			c=c-1;
	            			A=a+A;
	        				B=modifyStyle(b)+B;
	        			}else if(mi==2) {
	        				r=r-1;
	        				A=delStyle(a)+A;
	        				//B="_"+B;
	        			}else {
	        				c=c-1;
	        				//A="_"+A;
	        				B=addStyle(b)+B;
	        			}
	        		}
        		}
        	}
        }
        
       ;
        
        return  new String[] {A,B};// A+"<br>"+B;
    }
    
    public static int min1(int a,int b,int c) {//依次左上 上边 左边字符
    	int min = (a < b) ? a : b;
        min = (min < c) ? min : c;
        
        if(min==a) {
        	return 1;
        }else if(min==b) {
        	return 2;
        }
        else {
        	return 3;
        }
    }
    public static String addStyle(char value) {
    	return "<span style='color:blue'>"+value+"</span>";
    }
    public static String modifyStyle(char value) {
    	return "<span style='color:green'>"+value+"</span>";
    }
    public static String delStyle(char value) {
    	return "<span style='color:red'><del>"+value+"</del></span>";
    }
    
//    public static double sim(String str1, String str2) {
//        try {
//            double ld = (double)ld(str1, str2);
//            return (1-ld/(double)Math.max(str1.length(), str2.length()));
//        } catch (Exception e) {
//            return 0.1;
//        }
//    }
 
    public static void main(String[] args) {
    	
//    	String str1="GGATCGA";
//    	String str2="AATTCAGTTA";
    	
//        String str1 = "计算字符串都是20000字符串都是20000字符";
//        String str2 = "算字传串都是2000字符串都是20000字d符";
    	
//      String str1 = "GA";
//      String str2 = "GAGA";
        String str1 = "计算字符串都是20000字符串都是20000字符，则LD矩阵的大小为20000*200字符，则LD矩阵的大小为20000*200匹配字符串的话，时间复杂度为O(MN)，空间复杂度由于需要利用LD矩阵计算匹配路径，故空间复杂度仍然为O(MN)。这个在两个字符较短小的情况下，能获得不较短较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不得不较短小的情况下，能获得不小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不串较短小的情况下，能获得不都比较短小的情况下，能获得不错的性能。不过，如果字符串比较长的情况下，就需要极大的空";
        String str2 = "计算匹配字符串的话，时间复杂字符串都是20000字符，则LD矩阵的大小为20000*200字符串都是20000字符，则LD矩阵的大小为20000*200字符串都是20000字符，则LD矩阵的大小为20000*200字符串都是20000字符，则LD矩阵的大小为20000*200字符串都是20000字符，则LD矩阵的大小为20000*200字符串都是20000字符，则LD矩阵的大小为20000*200字符串都是20000字符，则LD矩阵的大小为20000*200度为O(MN)，空间复杂度由于需要利用LD矩阵计算匹配路径，故空间较短小的情况下，能获得不复杂度仍然为O(MN)。这个在两个字符串都比错的性能。不过，如果字符串比较长的情况下，就需较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不要极较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不较短小的情况下，能获得不大的空";
        trace(ld(str1, str2),str1,str2);
//        System.out.println("ld=" + ld(str1, str2));
//        System.out.println("sim=" + sim(str1, str2));
        String[] a=compare(str1, str2);
        System.out.println(a[0]+"<br>"+a[1]);
       
    }
}

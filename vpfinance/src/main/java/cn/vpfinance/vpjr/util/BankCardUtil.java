package cn.vpfinance.vpjr.util;

import java.util.ArrayList;

/**
 * Created by hsq on 16/5/11.
 */
public class BankCardUtil {

    /**
     * 校验银行卡是否正确
     *
     *  校验原理:
     *  取最后一位数
     *  把剩余的位数按:
     *  1. 奇数位*2 < 9 的数组之和
     *  2. 偶数位数组之和
     *  3. 奇数位*2 >9 的分割之后的数组个位数之和
     *  4. 奇数位*2 >9 的分割之后的数组十位数之和
     *  相加, 再以10减去(以上4个值之和整除10)
     *  看是否等于最后一位
     *
     * @param cardId
     * @return
     */
    public static boolean validateCard(String cardId){

        //16-19位
        if(cardId.length()<16 || cardId.length()>19){
            return false;
        }

        //取出最后一位（与luhm进行比较）
        int lastNum=Integer.parseInt(cardId.substring(cardId.length()-1,cardId.length()));

        //前15或18位
        String first15Num=cardId.substring(0,cardId.length()-1);

        ArrayList<String> newArr=new ArrayList<String>();

        //前15或18位倒序存进数组
        for(int i=first15Num.length();i>0;i--){
            newArr.add(first15Num.length()-i,first15Num.substring(i-1,i));
        }

        ArrayList oddList1 = new ArrayList();//奇数位*2的积 <9
        ArrayList oddList2 = new ArrayList();//奇数位*2的积 >9
        ArrayList evenList=new ArrayList();  //偶数位数组

        for(int j=0;j<newArr.size();j++){
            if((j+1)%2==1){
                //奇数位
                if(Integer.parseInt(newArr.get(j))*2<9) {
                    oddList1.add(Integer.parseInt(newArr.get(j)) * 2);
                }else {
                    oddList2.add(Integer.parseInt(newArr.get(j)) * 2);
                }
            }else{
                //偶数位
                evenList.add(Integer.parseInt(newArr.get(j)));
            }
        }

        //奇数位*2 >9 的分割之后的数组个位数
        ArrayList oddChild1=new ArrayList();

        //奇数位*2 >9 的分割之后的数组十位数
        ArrayList oddChild2=new ArrayList();

        for(int h=0;h<oddList2.size();h++){
            oddChild1.add(Integer.parseInt(String.valueOf(oddList2.get(h)))%10);
            oddChild2.add(Integer.parseInt(String.valueOf(oddList2.get(h)))/10);
        }

        int sumOdd=0; //奇数位*2 < 9 的数组之和
        int sumEven=0; //偶数位数组之和
        int sumOddChild1=0; //奇数位*2 >9 的分割之后的数组个位数之和
        int sumOddChild2=0; //奇数位*2 >9 的分割之后的数组十位数之和
        int sumTotal=0;

        for(int m=0;m<oddList1.size();m++){
            sumOdd=sumOdd+Integer.parseInt(String.valueOf(oddList1.get(m)));
        }

        for(int n=0;n<evenList.size();n++){
            sumEven=sumEven+Integer.parseInt(String.valueOf(evenList.get(n)));
        }

        for(int p=0;p<oddChild1.size();p++){
            sumOddChild1=sumOddChild1+Integer.parseInt(String.valueOf(oddChild1.get(p)));
            sumOddChild2=sumOddChild2+Integer.parseInt(String.valueOf(oddChild2.get(p)));
        }
        //计算总和
        sumTotal=sumOdd + sumEven + sumOddChild1 + sumOddChild2;

        //计算Luhm值
        int k= sumTotal%10==0?10:sumTotal%10;
        int luhm= 10-k;

        if(cardId.length()>=16){
            if(lastNum==luhm){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public static void main(String[] args){

        String[] cards =
        {"6214830000139732"};

        for(String cardId:cards) {
            System.out.println(validateCard(cardId));
        }
    }
}

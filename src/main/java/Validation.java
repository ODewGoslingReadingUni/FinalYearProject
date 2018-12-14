public class Validation {

    public static boolean isFloat(String input){
        try{
            Float.parseFloat(input);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}

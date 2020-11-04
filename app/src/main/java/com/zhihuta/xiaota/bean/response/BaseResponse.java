package com.zhihuta.xiaota.bean.response;

public class BaseResponse {

    //base response error code, default is 0.
    public  int errorCode = SUCCESS;

    //---------------------------------------//
    //define common error code
    public static final short  COMMON_MODULE_PREFIX=0x0000;

    //some predefined code:
    public static final int  SUCCESS =0x00000000;
    //some predefined code:
    public static final int  ERROR   =0x0000FFFF;

    //---------------------------------------//

//
//    /**
//     * Remove cached query result from redis
//     *
//     * @param module_prefix, 4 characters 16radix, e.g. common module, 0x0000
//     * @param code, real error code, 4 characters, 16 radix or else radix.
//     * @return long error, the combination of prefix and real code by or(|)
//     */
//    public static int makeErrorCode( short module_prefix, short code)
//    {
//        int error = (module_prefix<<16)|code;
//
//        return error;
//    }
//
//    public String getMessage(int  errorCode)
//    {
//        String strMsg = ErrorMessageServiceImpl.getsErrorMessage().get( errorCode);
//
//        return strMsg == null?"":strMsg;
//    }
//
//    public static String getMessageStatic(int  errorCode)
//    {
//        String strMsg = ErrorMessageServiceImpl.getsErrorMessage().get( errorCode);
//
//        return strMsg == null?"":strMsg;
//    }
}

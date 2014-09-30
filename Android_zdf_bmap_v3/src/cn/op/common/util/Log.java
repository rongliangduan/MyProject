/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.op.common.util;

import cn.op.zdf.AppContext;

/** 
 * Utility class for LogCat.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Log {
	private static final boolean DEBUG = AppContext.isDebugLog;  
    
    @SuppressWarnings("unchecked")
    public static String makeLogTag(Class cls) {
        return cls.getSimpleName();
    }
    
    
    public static void v(String tag, String msg) {  
        if(DEBUG) {  
            android.util.Log.v(tag, msg);  
        }  
    }  
    public static void v(String tag, String msg, Throwable tr) {  
        if(DEBUG) {  
            android.util.Log.v(tag, msg, tr);  
        }  
    }  
    public static void d(String tag, String msg) {  
        if(DEBUG) {  
            android.util.Log.d(tag, msg);  
        }  
    }  
    public static void d(String tag, String msg, Throwable tr) {  
        if(DEBUG) {  
            android.util.Log.d(tag, msg, tr);  
        }  
    }  
    public static void i(String tag, String msg) {  
        if(DEBUG) {  
            android.util.Log.i(tag, msg);  
        }  
    }  
    public static void i(String tag, String msg, Throwable tr) {  
        if(DEBUG) {  
            android.util.Log.i(tag, msg, tr);  
        }  
    }  
    public static void w(String tag, String msg) {  
        if(DEBUG) {  
            android.util.Log.w(tag, msg);  
        }  
    }  
    public static void w(String tag, String msg, Throwable tr) {  
        if(DEBUG) {  
            android.util.Log.w(tag, msg, tr);  
        }  
    }  
    public static void w(String tag, Throwable tr) {  
        if(DEBUG) {  
            android.util.Log.w(tag, tr);  
        }  
    }  
    public static void e(String tag, String msg) {  
        if(DEBUG) {  
            android.util.Log.e(tag, msg);  
        }  
    }  
    public static void e(String tag, String msg, Throwable tr) {  
        if(DEBUG) {  
            android.util.Log.e(tag, msg, tr);  
        }  
    }  

}

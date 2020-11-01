// IAdd.aidl
package com.example.aidlserver;
import com.example.aidlserver.IDBCallback;
// Declare any non-default types here with import statements

interface IAdd {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void getOrientationSenderData(IDBCallback callback);
}

To compile:

1. Download and install NetBeans (I tried before in Eclipse; didn't work)
2. Download the source code
3. Add the project in NetBeans
4. I got these errors:

- Problem: Project Problem: "JDOM" library could not be found, resolvable by: Resolver for LIBRARY libs.JDOM.classpath unresolved
- Problem: Project Problem: "DualRPC2" library could not be found, resolvable by: Resolver for LIBRARY libs.DualRPC2.classpath unresolved
-Problem: Project Problem: "OggVorbisSPI" library could not be found, resolvable by: Resolver for LIBRARY libs.OggVorbisSPI.classpath unresolved
- Problem: Project Problem: "JDK_1.5" platform could not be found, resolvable by: Resolver for PLATFORM JDK_1.5 unresolved

Solution:
- Point to your JDK (1.8 or whatever)
- Add the libraries are located under ..\InvadeEarth\web\
- Clean & Build

5. I got this error in InvadeEarth.java:

error: cannot find symbol import com.retrogui.dualrpc.common.CallException;


- If JNLP not working
Follow instructions from http://java.com/en/download/help/jcp_security.xml to add http://invadeearth.sourceforge.net as a security exception

package res;

import java.io.File;
import java.net.URL;

public class ResourceProducer {
	public static File getResourceByName(String resName) throws Exception {
		//System.out.println(ClassLoader.getSystemResource("").toString()); //bin
		URL url = ResourceProducer.class.getResource(resName);
		return new File(url.toURI());
	}
}

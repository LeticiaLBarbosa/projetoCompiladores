package examples;

public class ExemploTeste{

	public void begginTest(){
		boolean a = true;
	}

	public void logicalOP() {
		boolean t = true;
		boolean f = false;
		boolean a = t && f || t != true || t == f && !f;
		boolean b = t ^ f;

	}

	public void nullOp() {
		Object a = null;
	}

	public void integers() {
		int a = 10;
		int b = 20;
	}

	public void chars() {
		char a = 'A';
		char b = 'B';
	}

	/** Comment Test. Check that ignores */
	/**
	 * Comment test with symbols 987,) _ ++ _ {} ^ ` and more than one line
	 */

	public void integersOp() {
		int a = 10;
		int b = 20;
		float c = a + b - a * b / a % b;
		int d = a >> 2;
		int e = b >>> 3;
		int f = e << 2;
		float j = a /= 2;
		int g = a++;
		int p = b--;
	}

	public void floats() {
		float a = (float) 1.5;
		float b = (float) 1.5986;
		float c = (float) 0.5986;
	}

	public void strings(){
		String a = "";
		String b = "test";
		String c = "AaDa";
		String d = "Aa";
		String e = "A12";
		String f = "09";
		String g = "Mauhsuwswijsiks wuhedywghdwujsoqwks dhywgdywqgsuqjsiqjs uwhsdywgduhsiqjsqs**";
		String h = "Mauhsuwswijsiks wuhedywghdwujsoqwks "
				+ "dhywgdywqgsuqjsiqjs uwhsMauhsuwswijsiks wuhedywghdwujsoqwks "
				+ "dhywgdywqgsuqjsiqjs uwhs "
				+ "Mauhsuwswijsiks wuhedywghdwujsoqwks dhywgdywqgsuqjsiqjs uwhs";
		g = "!:_)(*&&*)oiiis";
	}

}
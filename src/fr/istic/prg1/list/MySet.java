package fr.istic.prg1.list;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import fr.istic.prg1.list_util.Comparison;
import fr.istic.prg1.list_util.Iterator;
// import fr.istic.prg1.list_util.List;
import fr.istic.prg1.list.List;
import fr.istic.prg1.list_util.SmallSet;

/**
 * @author Mickaël Foursov <foursov@univ-rennes1.fr>
 * @version 5.0
 * @since 2022-09-23
 */

public class MySet extends List<SubSet> {

	/**
	 * Borne superieure pour les rangs des sous-ensembles.
	 */
	private static final int MAX_RANG = 128;
	private static final String NEW_VALUE = " nouveau contenu :";

	/**
	 * Sous-ensemble de rang maximal à mettre dans le drapeau de la liste.
	 */
	private static final SubSet FLAG_VALUE = new SubSet(MAX_RANG, new SmallSet());
	/**
	 * Entrée standard.
	 */
	private static final Scanner standardInput = new Scanner(System.in);

	public MySet() {
		super();
		setFlag(FLAG_VALUE);
	}

	/**
	 * Fermer tout (actuellement juste l'entrée standard).
	 */
	public static void closeAll() {
		standardInput.close();
	}

	private static Comparison compare(int a, int b) {
		if (a < b) {
			return Comparison.INF;
		} else if (a == b) {
			return Comparison.EGAL;
		} else {
			return Comparison.SUP;
		}
	}

	public void print() {
		System.out.println(" [version corrigee de contenu]");
		this.print(System.out);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// //////////// Appartenance, Ajout, Suppression, Cardinal
	// ////////////////////
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * @return true si le nombre saisi par l'utilisateur appartient à this, false
	 *         sinon
	 */
	public boolean contains() {
		System.out.println("écrire l'éléménent à rechercher");
		Scanner sc = new Scanner(System.in);
		int x = sc.nextInt();
		sc.close();
		return contains(x);
	}

	/**
	 * @param element valeur à tester
	 * @return true si valeur appartient à l'ensemble, false sinon
	 */

	public boolean contains(int value) {
		int m = value % 256;
		int r = value / 256;
		Iterator<SubSet> it = this.iterator();
		while (it.getValue().rank < r) {
			it.goForward();
		}
		return it.getValue().rank == r && it.getValue().set.contains(m);
	}

/**
 * Ajouter à this toutes les valeurs saisis par l'utilisateur et afficher le
 * nouveau contenu.
 */
public void add() {
	System.out.println(" valeurs a ajouter (-1 pour finir) : ");
	this.add(System.in);
	System.out.println(NEW_VALUE);
	this.printNewState();
}

/**
 * Ajouter à this toutes les valeurs prises dans is.
 * 
 * @param is flux d'entrée.
 */
public void add(InputStream is) {
	try (Scanner s = new Scanner(is)) {
		while (s.hasNextInt()) {
			int v = s.nextInt();
			if (!(v >= 0 && v <= 32767)) {
				return;
			}
			addNumber(v);
		}
		s.close();
	}
}

/**
 * Ajouter element à this,
 *
 * @param element valuer à ajouter.
 */
public void addNumber(int value) {
	int m = value % 256;
	int r = value / 256;

	Iterator<SubSet> it = this.iterator();

	while (it.getValue().rank < r) {
		it.goForward();
	}

	if (it.getValue().rank == r) {
		it.getValue().set.add(m);
	} else {
		SubSet subSet = new SubSet(r, new SmallSet());
		subSet.set.add(m);
		it.addLeft(subSet);
	}
}

/**
 * Supprimer de this toutes les valeurs saisies par l'utilisateur et afficher le
 * nouveau contenu.
 */
public void remove() {
	System.out.println("  valeurs a supprimer (-1 pour finir) : ");
	this.remove(System.in);
	System.out.println(NEW_VALUE);
	this.printNewState();
}

/**
 * Supprimer de this toutes les valeurs prises dans is.
 * 
 * @param is flux d'entrée
 */
public void remove(InputStream is) {
	try (Scanner s = new Scanner(is)) {
		while (s.hasNextInt()) {
			int v = s.nextInt();
			if (!(v >= 0 && v <= 32767)) {
				return;
			}
			removeNumber(v);
		}
		s.close();
	}
}

/**
 * Supprimer element de this.
 * 
 * @param element valeur à supprimer
 */
public void removeNumber(int value) {
	Iterator<SubSet> it = this.iterator();
	int m = value % 256;
	int r = value / 256;
	while (!it.isOnFlag()) {
		if (it.getValue().rank == r) {
			it.getValue().set.remove(m);
			if (it.getValue().set.size() == 0) {
				it.remove();
			}
		}
		it.goForward();
	}

}

/**
 * @return taille de l'ensemble this
 */
public int size() {
	int counter = 0;
	Iterator<SubSet> it = this.iterator();
	while (!it.isOnFlag()) {
		counter += it.getValue().set.size();
		it.goForward();
	}
	return counter;
}

// /////////////////////////////////////////////////////////////////////////////
// /////// Difference, DifferenceSymetrique, Intersection, Union ///////
// /////////////////////////////////////////////////////////////////////////////

/**
 * This devient la différence de this et set2.
 * 
 * @param set2 deuxième ensemble
 */
public void difference(MySet set2) {
	if (this == set2) {
		this.clear();
		return;
	}

	Iterator<SubSet> itThis = this.iterator();
	Iterator<SubSet> itSet2 = set2.iterator();

	while (!itThis.isOnFlag()) {
		int rThis = itThis.getValue().rank;
		int rSet2 = itSet2.getValue().rank;
		if (rThis == rSet2) {
			itThis.getValue().set.difference(itSet2.getValue().set);
			if (itThis.getValue().set.isEmpty()) {
				itThis.remove();
			} else {
				itThis.goForward();
			}
			itSet2.goForward();
		} else if (rThis > rSet2) {
			itSet2.goForward();
		} else {
			itThis.goForward();
		}
	}

}

/**
 * This devient la différence symétrique de this et set2.
 * 
 * @param set2 deuxième ensemble
 */
public void symmetricDifference(MySet set2) {
	Iterator<SubSet> itThis = this.iterator();
	Iterator<SubSet> itSet2 = set2.iterator();

	while (!itThis.isOnFlag()) {
		if (this == set2) {
			this.clear();
			return;
		}
		int rThis = itThis.getValue().rank;
		int rSet2 = itSet2.getValue().rank;

		if (rThis == rSet2) {
			itThis.getValue().set.symmetricDifference(itSet2.getValue().set);
			if (itThis.getValue().set.isEmpty()) {
				itThis.remove();
			} else {
				itThis.goForward();
			}
			itSet2.goForward();
		} else if (rThis > rSet2) {
			itThis.addLeft(itSet2.getValue().copyOf());
			itThis.goForward();
			itSet2.goForward();
		} else {
			itThis.goForward();
		}

	}

	while (!itSet2.isOnFlag()) {
		itThis.addLeft(itSet2.getValue().copyOf());
		itSet2.goForward();
	}

}

/*
 * This devient l'intersection de this et set2.
 * 
 * @param set2 deuxième ensemble
 */
public void intersection(MySet set2) {
	Iterator<SubSet> itThis = this.iterator();
	Iterator<SubSet> itSet2 = set2.iterator();

	while (!itThis.isOnFlag()) {
		int rThis = itThis.getValue().rank;
		int rSet2 = itSet2.getValue().rank;

		if (rThis == rSet2) {
			itThis.getValue().set.intersection(itSet2.getValue().set);
			if (itThis.getValue().set.isEmpty()) {
				itThis.remove();
			} else {
				itThis.goForward();
			}
			itSet2.goForward();
		} else if (rThis < rSet2) {
			itThis.remove();
		} else {
			itSet2.goForward();
		}
	}
}

/**
 * This devient l'union de this et set2.
 * 
 * @param set2 deuxième ensemble
 */
public void union(MySet set2) {
	System.out.println("l'ensemble numero n1 = ");
	this.print(System.out);
	System.out.println("l'ensemble numero n2 = ");
	set2.print(System.out);

	Iterator<SubSet> itThis = this.iterator();
	Iterator<SubSet> itSet2 = set2.iterator();

	while (!itThis.isOnFlag()) {
		int rThis = itThis.getValue().rank;
		int rSet2 = itSet2.getValue().rank;

		if (rThis == rSet2) {
			itThis.getValue().set.union(itSet2.getValue().set);
			itThis.goForward();
			itSet2.goForward();
		} else if (rThis < rSet2) {
			itThis.goForward();
		} else {
			itThis.addLeft(new SubSet(rSet2, itSet2.getValue().set.copyOf()));
			itThis.goForward();
			itSet2.goForward();
		}
	}

	while (!itSet2.isOnFlag()) {
		itThis.addLeft(itSet2.getValue());
		itSet2.goForward();
	}

	System.out.println("apres union, l'ensemble numero n1 = ");
	this.printNewState();
}

// /////////////////////////////////////////////////////////////////////////////
// /////////////////// Egalite, Inclusion ////////////////////
// /////////////////////////////////////////////////////////////////////////////

/**
 * @param o deuxième ensemble
 * 
 * @return true si les ensembles this et o sont égaux, false sinon
 */
@Override
public boolean equals(Object o) {
	boolean b = true;
	if (this == o) {
		b = true;
	} else if (!(o instanceof MySet)) {
		b = false;
	} else {
		MySet mySet = (MySet) o;
		Iterator<SubSet> itThis = this.iterator();
		Iterator<SubSet> itMySet = mySet.iterator();

		if (this.size() != mySet.size()) {
			b = false;
		} else {
			boolean equalrank = true;
			boolean equalSmallSet = true;
			while (!itThis.isOnFlag() && equalSmallSet && equalrank) {
				if (itThis.getValue().rank != itMySet.getValue().rank) {
					equalrank = false;
				} else {
					equalSmallSet = itThis.getValue().set.equals(itMySet.getValue().set);
				}
				itThis.goForward();
				itMySet.goForward();
			}
			b = equalSmallSet && equalrank;
		}

	}
		return b;
	}

	/**
	 * @param set2 deuxième ensemble
	 * @return true si this est inclus dans set2, false sinon
	 */
	public boolean isIncludedIn(MySet set2) {
		Iterator<SubSet> itThis = this.iterator();
		Iterator<SubSet> itSet2 = set2.iterator();
		boolean isIncluded = true;
		while (!itThis.isOnFlag() && isIncluded) {
			int rThis = itThis.getValue().rank;
			int rSet2 = itSet2.getValue().rank;

			if (rThis == rSet2) {
				isIncluded = itThis.getValue().set.isIncludedIn(itSet2.getValue().set);
				itThis.goForward();
				itSet2.goForward();
			} else if (rThis < rSet2) {
				isIncluded = false;
			} else {
				itSet2.goForward();
			}
		}
		return isIncluded;
	}

	// /////////////////////////////////////////////////////////////////////////////
	// //////// Rangs, Restauration, Sauvegarde, Affichage //////////////
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * Afficher les rangs présents dans this.
	 */
	public void printRanks() {
		System.out.println(" [version corrigee de rangs]");
		this.printRanksAux();
	}

	private void printRanksAux() {
		int count = 0;
		Iterator<SubSet> it = this.iterator();
		StringBuilder line = new StringBuilder("Rangs presents : ");
		while (!it.isOnFlag()) {
			line.append(it.getValue().rank + "  ");
			count = count + 1;
			if (count == 10) {
				line.append("\n");
				count = 0;
			}
			it.goForward();

		}
		System.out.println(line.toString());
		if (count > 0) {
			System.out.println("\n");
		}
	}

	/**
	 * Créer this à partir d’un fichier choisi par l’utilisateur contenant une
	 * séquence d’entiers positifs terminée par -1 (cf f0.ens, f1.ens, f2.ens,
	 * f3.ens et f4.ens).
	 */
	public void restore() {
		String fileName = readFileName();
		InputStream inFile;
		try {
			inFile = new FileInputStream(fileName);
			System.out.println(" [version corrigee de restauration]");
			this.clear();
			this.add(inFile);
			inFile.close();
			System.out.println(NEW_VALUE);
			this.printNewState();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("fichier " + fileName + " inexistant");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier " + fileName);
		}
	}

	/**
	 * Sauvegarder this dans un fichier d’entiers positifs terminé par -1.
	 */
	public void save() {
		System.out.println(" [version corrigee de sauvegarde]");
		OutputStream outFile;
		try {
			outFile = new FileOutputStream(readFileName());
			this.print(outFile);
			outFile.write("-1\n".getBytes());
			outFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("pb ouverture fichier lors de la sauvegarde");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("probleme de fermeture du fichier");
		}
	}

	/**
	 * @return l'ensemble this sous forme de chaîne de caractères.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		int count = 0;
		SubSet subSet;
		int startValue;
		Iterator<SubSet> it = this.iterator();
		while (!it.isOnFlag()) {
			subSet = it.getValue();
			startValue = subSet.rank * 256;
			for (int i = 0; i < 256; ++i) {
				if (subSet.set.contains(i)) {
					StringBuilder number = new StringBuilder(String.valueOf(startValue + i));
					int numberLength = number.length();
					for (int j = 6; j > numberLength; --j) {
						number.append(" ");
					}
					result.append(number);
					++count;
					if (count == 10) {
						result.append("\n");
						count = 0;
					}
				}
			}
			it.goForward();
		}
		if (count > 0) {
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * Imprimer this dans outFile.
	 *
	 * @param outFile flux de sortie
	 */
	private void print(OutputStream outFile) {
		try {
			String string = this.toString();
			outFile.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Afficher l'ensemble avec sa taille et les rangs présents.
	 */
	private void printNewState() {
		this.print(System.out);
		int size = this.size();
		System.out.println("Nombre d'elements : " + size);
		this.printRanksAux();
	}

	/**
	 * @param scanner
	 * @param min     valeur minimale possible
	 * @return l'entier lu au clavier (doit être entre min et 32767)
	 */
	private static int readValue(Scanner scanner, int min) {
		int value = scanner.nextInt();
		while (value < min || value > 32767) {
			System.out.println("valeur incorrecte");
			value = scanner.nextInt();
		}
		return value;
	}

	/**
	 * @return nom de fichier saisi psar l'utilisateur
	 */
	private static String readFileName() {
		System.out.println(" nom du fichier : ");
		return standardInput.next();
	}

}
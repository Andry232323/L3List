package fr.istic.prg1.list;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import fr.istic.prg1.list_util.Iterator;
import fr.istic.prg1.list_util.SuperT;
import fr.istic.prg1.list_util.List.ListIterator;

public class List<T extends SuperT> {
	// liste en double chainage par references

	private class Element {
		// element de List<Item> : (Item, Element, Element)
		public T value;
		public Element left, right;

		public Element() {
			value = null;
			left = null;
			right = null;
		}

		public Element(T value, Element left, Element right) {
			this.value = value;
			this.left = left;
			this.right = right;
		}

	} // class Element

	public class ListIterator implements Iterator<T> {
		private Element current;

		private ListIterator() {
			current = flag;
			goForward();
		}

		@Override
		public void goForward() {
			current = current.right;
		}

		@Override
		public void goBackward() {
			current = current.left;
		}

		@Override
		public void restart() {
			while (current != flag) {
				goForward();
			}
			goForward();
		}

		@Override
		public boolean isOnFlag() {
			return current == flag;
		}

		@Override
		public void remove() {
			try {
				assert current != flag : "\n\n\nimpossible de retirer le drapeau\n\n\n";
			} catch (AssertionError e) {
				e.printStackTrace();
				System.exit(0);
			}

			Element tmp = current;
			goBackward();
			current.right = tmp.right;
		}

		@Override
		public T getValue() {
			return current.value;
		}

		@Override
		public T nextValue() {
			goForward();
			return current.value;
		}

		@Override
		public void addLeft(T v) {
			Element tmp = current.left;
			current.left = new Element(v, tmp, current);

		}

		@Override
		public void addRight(T v) {
			Element tmp = current.right;
			current.right = new Element(v, current, tmp);
		}

		@Override
		public void setValue(T v) {
			current.value = v;
		}

		@Override
		public void selfDestroy() {
		}

		@Override
		public String toString() {
			return "parcours de liste : pas d'affichage possible \n";
		}

	} // class IterateurListe

	private Element flag;

	private ArrayList<ListIterator> itList = new ArrayList<>();

	public List() {
		flag = new Element();
		flag.right = flag;
		flag.left = flag;
	}

	public ListIterator iterator() {
		ListIterator it = new ListIterator();
		itList.add(it);
		return it;
	}

	public boolean isEmpty() {
		return flag.left == flag && flag.right == flag;
	}

	public void clear() {
		flag.right = flag;
		flag.left = flag;
	}

	public void setFlag(T v) {
		flag.value = (T) v.copyOf();
	}

	public void addHead(T v) {
		Element e = new Element(v, null, null);
		Element oldHead = flag.right;
		flag.right = e;
		e.right = oldHead;
		e.left = flag;
	}

	public void addTail(T v) {
		Element e = new Element(v, null, null);
		Element oldTail = flag.left;
		flag.left = e;
		e.left = oldTail;
		e.right = flag;
	}

	@SuppressWarnings("unchecked")
	public List<T> clone() {
		List<T> nouvListe = new List<T>();
		ListIterator p = iterator();
		while (!p.isOnFlag()) {
			nouvListe.addTail((T) p.getValue().copyOf());
			// UNE COPIE EST NECESSAIRE !!!
			p.goForward();
		}
		p.selfDestroy();
		return nouvListe;
	}

	@Override
	public String toString() {
		String s = "contenu de la liste : \n";
		ListIterator p = iterator();
		while (!p.isOnFlag()) {
			s = s + p.getValue().toString() + " ";
			p.goForward();
		}
		p.selfDestroy();
		return s;
	}
}

package at.jku.dke.task_app.fanf.evaluation.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;


public class AttributeCollator implements Serializable, Comparator<String> {

	public int compare(String o1, String o2) {
		return Collator.getInstance().compare(o1, o2);
	}

}

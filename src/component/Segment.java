package component;

import java.util.LinkedHashSet;

public class Segment {
	LinkedHashSet<Integer> logSegment = null;
	Double value = 1.0;
	
	public Segment(LinkedHashSet<Integer> logSegment,Double value){
		this.logSegment = logSegment;
		this.value = value;
	}
	
	public void addValue(int value){
		this.value += value;
	}
	
	public void addSegment(LinkedHashSet<Integer> seg){
		this.logSegment.addAll(seg);
	}
	public LinkedHashSet<Integer> getLogSegment() {
		return logSegment;
	}

	public void setLogSegment(LinkedHashSet<Integer> logSegment) {
		this.logSegment = logSegment;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return logSegment.toString() + "=" + value;
	}
}

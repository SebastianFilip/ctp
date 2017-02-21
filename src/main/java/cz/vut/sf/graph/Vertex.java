package cz.vut.sf.graph;

public class Vertex {
	public final int id;
	
	public Vertex(int id) {
		super();
		this.id = id;
	}

	public Vertex getById(int lookingId){
		if(this.id == lookingId){
			return this;
		}
		return null;
	}

	@Override
	public String toString() {
		return "V" + id;
	}
}

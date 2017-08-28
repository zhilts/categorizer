package model;

public class DocumentUnit extends Unit {

	private Document document;

	public DocumentUnit(Document document) {
		this.document = document;
	}

	@Override
	public String getName() {
		return document.getDescription();
	}

	@Override
	public UnitType getType() {
		return UnitType.UnitTypeFile;
	}

	public Document getDocument() {
		return document;
	}

	public double getValueByDimention(Term term) {
		return document.getTfidfByTerm(term);
	}

	@Override
	public Unit getSimple() {
		return this;
	}
}

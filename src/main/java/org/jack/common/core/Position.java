package org.jack.common.core;

import org.springframework.util.Assert;

public class Position {
    private final int rowIndex;
    private final int cellIndex;
    public Position(int rowIndex,int cellIndex){
        Assert.state(rowIndex>=0, "rowIndex 必须大于等于0");
        Assert.state(cellIndex>=0, "cellIndex 必须大于等于0");
        this.rowIndex=rowIndex;
        this.cellIndex=cellIndex;
    }
    public int getRowIndex() {
        return rowIndex;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public Range cellRange(int cdiff){
        return new Range(this,new Position(rowIndex,cellIndex+cdiff));
    }
    public Range range(int rdiff,int cdiff){
        return new Range(this,new Position(rowIndex+rdiff,cellIndex+cdiff));
    }
    public Range rowRange(int rdiff){
        return new Range(this,new Position(rowIndex+rdiff,cellIndex));
    }
    public boolean sameCell(Position position){
        return cellIndex==position.cellIndex;
    }
    public boolean sameRow(Position position){
        return rowIndex==position.rowIndex;
    }
    public Position right(){
        return right(1);
    }
    public Position right(int i){
        return new Position(rowIndex,cellIndex+i); 
    }
    public Position left(){
        return left(1);
    }
    public Position left(int i){
        if(cellIndex==0){
            return null;
        }
        return new Position(rowIndex,cellIndex-i); 
    }
    public Position top(){
        return top(1);
    }
    public Position top(int i){
        if(rowIndex==0){
            return null;
        }
        return new Position(rowIndex-i,cellIndex);
    }
    public Position bottom(){
        return bottom(1);
    }
    public Position bottom(int i){
        return new Position(rowIndex+i,cellIndex);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cellIndex;
        result = prime * result + rowIndex;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Position other = (Position) obj;
        if (cellIndex != other.cellIndex)
            return false;
        if (rowIndex != other.rowIndex)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Position [rowIndex=" + rowIndex + ", cellIndex=" + cellIndex + "]";
    }
    
}
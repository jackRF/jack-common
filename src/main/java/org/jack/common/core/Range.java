package org.jack.common.core;

public class Range {
    private final Position position1;
    private final Position position2;

    public Range(Position position1, Position position2) {
        this.position1=position1;
        this.position2=position2;
    }
    public boolean inRange(Range range){
        int maxRowIndex=getMaxRowIndex();
        int minRowIndex=getMinRowIndex();
        if(minRowIndex<=range.getMinRowIndex()&&range.getMaxRowIndex()<=maxRowIndex){
            int maxCellIndex=getMaxCellIndex();
            int minCellIndex=getMinCellIndex();
            if(minCellIndex<=range.getMinCellIndex()&&range.getMaxCellIndex()<=maxCellIndex){
                return true;
            }
        }
        return false;

    }
    private int getMaxCellIndex(){
        return Math.max(position1.getCellIndex(), position2.getCellIndex());
    }
    private int getMinCellIndex(){
        return Math.min(position1.getCellIndex(), position2.getCellIndex());
    }
    private int getMaxRowIndex(){
        return Math.max(position1.getRowIndex(), position2.getRowIndex());
    }
    private int getMinRowIndex(){
        return Math.min(position1.getRowIndex(), position2.getRowIndex());
    }
	public boolean inRange(Position position){
        int ri=position.getRowIndex();
        int ci=position.getCellIndex();

        if(inRange(position1.getRowIndex(),position2.getRowIndex(),ri)){
            if(inRange(position1.getCellIndex(),position2.getCellIndex(),ci)){
                return true;
            }
        }
        return false;
    }
    private static boolean inRange(int v1,int v2,int value){
        int min=Math.min(v1, v2);
        int max=Math.max(v1, v2);
        if(min<=value&&value<=max){
            return true;
        }
        return false;
    }

    public Position getPosition1() {
        return position1;
    }

    public Position getPosition2() {
        return position2;
    }
}
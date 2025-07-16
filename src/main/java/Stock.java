import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
public class Stock {

    public enum UnitType{
        GRAMM,
        MILLILITER,
        PIECE
    }
    @Getter
    BigDecimal amount = new  BigDecimal(0);
    final UnitType unitType;

    Stock(UnitType unitType){
        this.unitType = unitType;
    }

    Stock(UnitType unitType, BigDecimal amount){
        this.unitType = unitType;
        throwIfDecimalPieces(amount);
        this.amount = amount;
    }

    public BigDecimal reduceAmount(BigDecimal amount){
        throwIfDecimalPieces(amount);
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if(this.amount.compareTo(amount) <= 0){
            throw new IllegalArgumentException("Amount exceeds stock");
        }
        this.amount = this.amount.subtract(amount);
        return this.amount;
    }

    public BigDecimal increaseAmount(BigDecimal amount){
        throwIfDecimalPieces(amount);
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount exceeds stock");
        }
        this.amount = this.amount.add(amount);
        return this.amount;
    }

    private void throwIfDecimalPieces(BigDecimal amount){
        if(!checkValidForPiece(amount)){
            throw new IllegalArgumentException("Invalid amount: " + amount.doubleValue() + ", pieces cannot be broken up.");
        }
    }

    private boolean checkValidForPiece(BigDecimal amount){
        return this.unitType == UnitType.PIECE && amount.intValueExact() == amount.doubleValue() ;
    }
}

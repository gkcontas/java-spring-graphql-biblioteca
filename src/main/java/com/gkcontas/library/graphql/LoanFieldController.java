package com.gkcontas.library.graphql;

import com.gkcontas.library.model.Loan;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LoanFieldController {

    @SchemaMapping(typeName = "Loan", field = "loanDate")
    public String loanDate(Loan loan) {
        return loan.getLoanDate().toString();
    }

    @SchemaMapping(typeName = "Loan", field = "returnDate")
    public String returnDate(Loan loan) {
        return loan.getReturnDate() != null ? loan.getReturnDate().toString() : null;
    }
}

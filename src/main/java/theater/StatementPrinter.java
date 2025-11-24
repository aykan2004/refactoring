package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    private final Invoice invoice;
    @SuppressWarnings({"checkstyle:DeclarationOrder", "checkstyle:SuppressWarnings"})
    private static Map<String, Play> plays = Map.of();

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        StatementPrinter.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings", "checkstyle:MagicNumber", "checkstyle:LineLength", "checkstyle:NeedBraces", "checkstyle:MultipleStringLiterals"})
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;
        StringBuilder result = new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());

        NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance p : invoice.getPerformances()) {

            // add volume credits
            volumeCredits += getVolumeCredits(p);
            // add extra credit for every five comedy attendees

            // print line for this order
            result.append(String.format("  %s: %s (%s seats)%n", getPlay(p).getName(), frmt.format(getAmount(p) / 100), p.getAudience()));
            totalAmount += getAmount(p);
        }
        result.append(String.format("Amount owed is %s%n", frmt.format(totalAmount / 100)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    @SuppressWarnings({"checkstyle:ParameterName", "checkstyle:SuppressWarnings"})
    private static Play getPlay(Performance p) {
        return plays.get(p.getPlayID());
    }

    @SuppressWarnings({"checkstyle:ParameterName", "checkstyle:SuppressWarnings", "checkstyle:MagicNumber", "checkstyle:SingleSpaceSeparator"})
    private static int getAmount(Performance performance) {
        int result;
        switch (getPlay(performance).getType()) {
            case "tragedy":
                result = 40000;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += 1000 * (performance.getAudience() - 30);
                }
                break;
            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;
            default:
                throw new RuntimeException(String.format("unknown type: %s",  getPlay(performance).getType()));
        }
        return result;
    }

    /**
     * Calculates the volume credits for a single performance.
     *
     * @param performance the performance to calculate credits for
     * @return the volume credits earned
     */
    private int getVolumeCredits(Performance performance) {
        int result = Math.max(
                performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);

        if ("comedy".equals(getPlay(performance).getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }

        return result;
    }

}

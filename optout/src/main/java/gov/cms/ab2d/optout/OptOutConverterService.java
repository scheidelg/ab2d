package gov.cms.ab2d.optout;

import gov.cms.ab2d.common.model.OptOut;

import java.util.List;

public interface OptOutConverterService {
    /**
     * Given a line from a file,
     * converts the string into a OptOut object that can be persisted in the database.
     *
     * @param line - line in the file
     *
     * @return the list of discovered opted out patients
     */
    List<OptOut> convert(String line);
}

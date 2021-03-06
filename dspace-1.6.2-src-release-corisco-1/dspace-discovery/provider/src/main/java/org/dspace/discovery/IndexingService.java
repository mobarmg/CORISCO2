/**
 * $Id: IndexingService.java 5119 2010-06-16 09:28:50Z benbosman $
 * $URL: http://scm.dspace.org/svn/repo/modules/dspace-discovery/trunk/provider/src/main/java/org/dspace/discovery/IndexingService.java $
 * *************************************************************************
 * Copyright (c) 2002-2009, DuraSpace.  All rights reserved
 * Licensed under the DuraSpace License.
 *
 * A copy of the DuraSpace License has been included in this
 * distribution and is available at: http://scm.dspace.org/svn/repo/licenses/LICENSE.txt
 */
package org.dspace.discovery;

import org.apache.solr.client.solrj.SolrServerException;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;

import java.io.IOException;
import java.sql.SQLException;

/**
 * User: mdiggory
 * Date: Oct 19, 2009
 * Time: 12:51:53 PM
 */
public interface IndexingService {

    void indexContent(Context context, DSpaceObject dso)
            throws SQLException;

    void indexContent(Context context, DSpaceObject dso,
                      boolean force) throws SQLException;

    void unIndexContent(Context context, DSpaceObject dso)
            throws SQLException, IOException;

    void unIndexContent(Context context, String handle)
            throws SQLException, IOException;

    void unIndexContent(Context context, String handle, boolean commit)
            throws SQLException, IOException;

    void reIndexContent(Context context, DSpaceObject dso)
            throws SQLException, IOException;

    void createIndex(Context context) throws SQLException, IOException;

    void updateIndex(Context context);

    void updateIndex(Context context, boolean force);

    void cleanIndex(boolean force) throws IOException,
            SQLException, SearchServiceException;
}

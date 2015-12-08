package ch.cyberduck.core.local;

import ch.cyberduck.core.AbstractTestCase;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.io.watchservice.DisabledWatchService;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @version $Id$
 */
public class FileWatcherTest extends AbstractTestCase {

    @Test
    public void testMatchDefaultLocal() throws IOException {
        assertTrue(
                new FileWatcher(new DisabledWatchService()).matches(
                        new Local("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"),
                        new Local("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
        assertTrue(
                new FileWatcher(new DisabledWatchService()).matches(
                        new Local("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"),
                        new Local("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
        assertFalse(
                new FileWatcher(new DisabledWatchService()).matches(
                        new Local("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"),
                        new Local("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/d/edit.html"))
        );
        assertFalse(
                new FileWatcher(new DisabledWatchService()).matches(
                        new Local("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/d/edit.html"),
                        new Local("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
        assertTrue(
                new FileWatcher(new DisabledWatchService()).matches(
                        new Local("edit.html"),
                        new Local("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
        assertTrue(
                new FileWatcher(new DisabledWatchService()).matches(
                        new Local("edit.html"),
                        new Local("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
    }

    @Test
    public void testMatchFinderLocal() throws IOException {
        assertTrue(
                new FileWatcher(new DisabledWatchService()).matches(
                        new FinderLocal("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"),
                        new FinderLocal("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
        assertTrue(
                new FileWatcher(new DisabledWatchService()).matches(
                        new FinderLocal("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"),
                        new FinderLocal("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
        assertFalse(
                new FileWatcher(new DisabledWatchService()).matches(
                        new FinderLocal("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"),
                        new FinderLocal("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/d/edit.html"))
        );
        assertFalse(
                new FileWatcher(new DisabledWatchService()).matches(
                        new FinderLocal("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/d/edit.html"),
                        new FinderLocal("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
        assertTrue(
                new FileWatcher(new DisabledWatchService()).matches(
                        new FinderLocal("edit.html"),
                        new FinderLocal("/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
        assertTrue(
                new FileWatcher(new DisabledWatchService()).matches(
                        new FinderLocal("edit.html"),
                        new FinderLocal("/private/var/folders/cl/622z57616532npsw3xs1ndyc0000gp/T/1022b1a9-c21c-4a79-9162-59990c75aaa8/usr/home/dkocher/sandbox/edit.html"))
        );
    }
}
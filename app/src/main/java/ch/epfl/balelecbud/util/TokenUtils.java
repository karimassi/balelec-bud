package ch.epfl.balelecbud.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import ch.epfl.balelecbud.BalelecbudApplication;
import ch.epfl.balelecbud.util.database.FirestoreDatabaseWrapper;

public class TokenUtils {

    public static String getToken(String uid) throws ExecutionException, InterruptedException {
        AtomicReference<String> token = null;
        BalelecbudApplication.getAppDatabaseWrapper()
                .getCustomDocument(FirestoreDatabaseWrapper.TOKENS_PATH, uid, String.class)
                .whenComplete((t, throwable) -> {
                    if( t != null ) {
                        token.set(t);
                    }
                    else throwable.printStackTrace();
                });
        return token.get();
    }
}

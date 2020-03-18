package ch.epfl.balelecbud.authentication;

import android.app.Activity;
import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.internal.firebase_auth.zzff;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzy;
import com.google.firebase.auth.zzz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class MockAuthenticator implements Authenticator {

    private static final Authenticator instance = new MockAuthenticator();

    private boolean loggedIn;

    private final Map<String, String> users = new HashMap<String, String>() {
        {
            put("karim@epfl.ch", "123456");
        }
    };

    private MockAuthenticator() {
        loggedIn = false;
    }

    @Override
    public void signIn(final String email, final String password, OnCompleteListener callback) {
        if (users.containsKey(email) && users.get(email).equals(password)) setLoggedIn(true);
        callback.onComplete(new Task() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public boolean isSuccessful() {
                return users.containsKey(email) && users.get(email).equals(password);
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public Object getResult() {
                return null;
            }

            @Nullable
            @Override
            public Object getResult(@NonNull Class aClass) throws Throwable {
                return null;
            }

            @Nullable
            @Override
            public Exception getException() {
                if (!users.containsKey(email)) return new Exception("User does not exist");
                return null;
            }

            @NonNull
            @Override
            public Task addOnSuccessListener(@NonNull OnSuccessListener onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return this;
            }
        });
    }

    @Override
    public void createAccount(final String email, final String password, OnCompleteListener callback) {
        if (!users.containsKey(email)) {
            users.put(email, password);
            setLoggedIn(true);
        }
        callback.onComplete(new Task() {
            @Override
            public boolean isComplete() {
                return false;
            }

            @Override
            public boolean isSuccessful() {
                return loggedIn && users.containsKey(email);
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public Object getResult() {
                return null;
            }

            @Nullable
            @Override
            public Object getResult(@NonNull Class aClass) throws Throwable {
                return null;
            }

            @Nullable
            @Override
            public Exception getException() {
                if (users.containsKey(email)) return new Exception("Account already exists!");
                return null;
            }

            @NonNull
            @Override
            public Task addOnSuccessListener(@NonNull OnSuccessListener onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener onSuccessListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return this;
            }

            @NonNull
            @Override
            public Task addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return this;
            }
        });
    }

    @Override
    public void signOut() {
        setLoggedIn(false);
    }

    @Override
    public FirebaseUser getCurrentUser() {
        if (loggedIn) {
            return new FirebaseUser() {
                @NonNull
                @Override
                public String getUid() {
                    return null;
                }

                @NonNull
                @Override
                public String getProviderId() {
                    return null;
                }

                @Override
                public boolean isAnonymous() {
                    return false;
                }

                @Nullable
                @Override
                public List<String> zza() {
                    return null;
                }

                @NonNull
                @Override
                public List<? extends UserInfo> getProviderData() {
                    return null;
                }

                @NonNull
                @Override
                public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
                    return null;
                }

                @Override
                public FirebaseUser zzb() {
                    return null;
                }

                @NonNull
                @Override
                public FirebaseApp zzc() {
                    return null;
                }


                @Nullable
                @Override
                public String getDisplayName() {
                    return null;
                }

                @Nullable
                @Override
                public Uri getPhotoUrl() {
                    return null;
                }

                @Nullable
                @Override
                public String getEmail() {
                    return null;
                }

                @Nullable
                @Override
                public String getPhoneNumber() {
                    return null;
                }

                @Nullable
                @Override
                public String zzd() {
                    return null;
                }

                @NonNull
                @Override
                public zzff zze() {
                    return null;
                }

                @Override
                public void zza(@NonNull zzff zzff) {

                }

                @NonNull
                @Override
                public String zzf() {
                    return null;
                }

                @NonNull
                @Override
                public String zzg() {
                    return null;
                }

                @Nullable
                @Override
                public FirebaseUserMetadata getMetadata() {
                    return null;
                }

                @NonNull
                @Override
                public zzz zzh() {
                    return null;
                }

                @Override
                public void zzb(List<zzy> list) {

                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {

                }

                @Override
                public boolean isEmailVerified() {
                    return false;
                }
            };
        }
        return null;
    }

    private void setLoggedIn(boolean state) {
        loggedIn = state;
    }

    public static Authenticator getInstance(){
        return instance;
    }


}

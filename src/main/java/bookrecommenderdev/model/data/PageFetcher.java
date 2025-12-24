package bookrecommenderdev.model.data;

import java.rmi.RemoteException;

@FunctionalInterface
public interface PageFetcher<T> {
  PageResult<T> fetch(int pageIndex) throws RemoteException;
}

package com.imangazalievm.bubbble.presentation.mvp.presenters;

import com.imangazalievm.bubbble.domain.interactors.ShotZoomInteractor;
import com.imangazalievm.bubbble.domain.models.Images;
import com.imangazalievm.bubbble.presentation.commons.permissions.Permission;
import com.imangazalievm.bubbble.presentation.commons.permissions.PermissionRequestListener;
import com.imangazalievm.bubbble.presentation.commons.permissions.PermissionResult;
import com.imangazalievm.bubbble.presentation.commons.permissions.PermissionsManager;
import com.imangazalievm.bubbble.presentation.mvp.views.ShotZoomView;
import com.imangazalievm.bubbble.presentation.mvp.views.ShotZoomView$$State;
import com.imangazalievm.bubbble.test.BubbbleTestRunner;
import com.imangazalievm.bubbble.test.TestRxSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import io.reactivex.Completable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(BubbbleTestRunner.class)
public class ShotZoomPresenterTest {

    private static final String TEST_SHOT_TITLE = "Test title";
    private static final String TEST_SHOT_URL = "https://test-url.com/516";
    private static final String TEST_SHOT_IMAGE_URL = "https://test-url.com/image.png";

    @Mock
    ShotZoomInteractor shotZoomInteractorMock;
    @Mock
    ShotZoomView shotZoomViewMock;
    @Mock
    ShotZoomView$$State shotZoomViewStateMock;
    @Mock
    PermissionsManager permissionsManagerMock;

    private ShotZoomPresenter shotZoomPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        shotZoomPresenter = new ShotZoomPresenter(shotZoomInteractorMock, new TestRxSchedulerProvider(),
                TEST_SHOT_TITLE, TEST_SHOT_URL, TEST_SHOT_IMAGE_URL);
        shotZoomPresenter.setViewState(shotZoomViewStateMock);
        shotZoomPresenter.setPermissionsManager(permissionsManagerMock);
    }

    @Test
    public void shot_shouldLoadShotOnFirstAttach() {
        //arrange

        //act
        shotZoomPresenter.onFirstViewAttach();

        // assert
        verify(shotZoomViewStateMock).showLoadingProgress();
        verify(shotZoomViewStateMock).showShotImage(TEST_SHOT_IMAGE_URL);
    }

    @Test
    public void onImageLoadSuccess_shouldHideProgress() {
        //arrange

        // act
        shotZoomPresenter.onFirstViewAttach();
        shotZoomPresenter.onImageLoadSuccess();

        // assert
        verify(shotZoomViewStateMock).hideLoadingProgress();
    }

    @Test
    public void onImageLoadError_shouldShowErrorLayout() {
        //arrange

        // act
        shotZoomPresenter.onFirstViewAttach();
        shotZoomPresenter.onImageLoadError();

        // assert
        verify(shotZoomViewStateMock).hideLoadingProgress();
        verify(shotZoomViewStateMock).showErrorLayout();
    }

    @Test
    public void onDownloadImageClicked_shouldSaveImageOnPermissionGranted() {
        //arrange
        Answer<Void> permissionRequestAnswer = invocation -> {
            PermissionRequestListener listener = (PermissionRequestListener) invocation.getArguments()[1];
            listener.onResult(new PermissionResult(Permission.READ_EXTERNAL_STORAGE, true, true));
            return null;
        };

        doAnswer(permissionRequestAnswer)
                .when(permissionsManagerMock)
                .requestPermission(eq(Permission.READ_EXTERNAL_STORAGE), any(PermissionRequestListener.class));

        Images images = mock(Images.class);
        when(images.best()).thenReturn(TEST_SHOT_IMAGE_URL);
        when(shotZoomInteractorMock.saveImage(TEST_SHOT_IMAGE_URL))
                .thenReturn(Completable.complete());

        // act
        shotZoomPresenter.onFirstViewAttach();
        shotZoomPresenter.onDownloadImageClicked();

        // assert
        verify(shotZoomInteractorMock).saveImage(TEST_SHOT_IMAGE_URL);
    }

    @Test
    public void onDownloadImageClicked_shouldShowStorageAccessRationaleMessage() {
        //arrange
        Answer<Void> permissionRequestAnswer = invocation -> {
            PermissionRequestListener listener = (PermissionRequestListener) invocation.getArguments()[1];
            listener.onResult(new PermissionResult(Permission.READ_EXTERNAL_STORAGE, false, true));
            return null;
        };

        doAnswer(permissionRequestAnswer)
                .when(permissionsManagerMock)
                .requestPermission(eq(Permission.READ_EXTERNAL_STORAGE), any(PermissionRequestListener.class));

        // act
        shotZoomPresenter.onFirstViewAttach();
        shotZoomPresenter.onDownloadImageClicked();

        // assert
        verify(shotZoomViewStateMock).showStorageAccessRationaleMessage();
    }

    @Test
    public void onDownloadImageClicked_shouldShowAllowStorageAccessMessage() {
        //arrange
        Answer<Void> permissionRequestAnswer = invocation -> {
            PermissionRequestListener listener = (PermissionRequestListener) invocation.getArguments()[1];
            listener.onResult(new PermissionResult(Permission.READ_EXTERNAL_STORAGE, false, false));
            return null;
        };

        doAnswer(permissionRequestAnswer)
                .when(permissionsManagerMock)
                .requestPermission(eq(Permission.READ_EXTERNAL_STORAGE), any(PermissionRequestListener.class));

        // act
        shotZoomPresenter.onFirstViewAttach();
        shotZoomPresenter.onDownloadImageClicked();

        // assert
        verify(shotZoomViewStateMock).showAllowStorageAccessMessage();
    }

    @Test
    public void onAppSettingsButtonClicked_shouldOpenAppSettingsScreen() {
        //arrange

        // act
        shotZoomPresenter.onFirstViewAttach();
        shotZoomPresenter.onAppSettingsButtonClicked();

        // assert
        verify(shotZoomViewStateMock).openAppSettingsScreen();
    }


    @Test
    public void onShareShotClick_shouldShowShotSharing() {
        //arrange

        // act
        shotZoomPresenter.onFirstViewAttach();
        shotZoomPresenter.onShareShotClicked();

        // assert
        verify(shotZoomViewStateMock).showShotSharing(TEST_SHOT_TITLE, TEST_SHOT_URL);
    }

    @Test
    public void onOpenShotInBrowserClicked_shouldOpenBrowserScreen() {
        //arrange

        // act
        shotZoomPresenter.onFirstViewAttach();
        shotZoomPresenter.onOpenInBrowserClicked();

        // assert
        verify(shotZoomViewStateMock).openInBrowser(TEST_SHOT_URL);
    }

}

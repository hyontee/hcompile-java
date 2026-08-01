package ru.matrp.launcher.fragment;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hzy.libp7zip.P7ZipApi;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.launcher.model.FilesList;
import ru.matrp.launcher.network.Helper;
import ru.matrp.launcher.other.Utils;
import ru.matrp.bonus.R;
import ru.matrp.bonus.core.GTASA;

public class DownloadNeFragment extends MainActivity {

    public ConstraintLayout main_layout;
    public ImageView download_logo;
    public ImageView download_render;
    public TextView download_guide_text;
    public ProgressBar download_progressbar;
    public TextView download_text;
    public TextView faq_text;
    public List<String> urlsst;
    public List<String> toUnnZip;
    public List<String> unnZip;
    public boolean load = false;
    int i = 0;
    public int idText = 0;
    public int idImage = 0;
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    public final String[] TextInfo = {
            "МАТРЁШКА - лучшая игра жанра Role Play, открывшая за 2 года 17 серверов.",
            "В последнем обновлении вам больше не нужно каждый раз вводить пароль заходя на сервер.",
            "У вас есть технические или игровые вопросы? Нажмите на текст внизу экрана.",
            "Команда опытных разработчиков день и ночь работает для обеспечения комфортной игры каждому",
            "На главной странице можно узнать последние новости игры. Так же в настройках можно найти ссылки на наши социальные сети.",
            "У нас доступна удобнейшая авторизация через Google и ВКонтакте."
    };

    public DownloadNeFragment() {
        super();
        downloadInit();
    }

    public void downloadInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_download, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        main_layout = (ConstraintLayout) viewGroup.findViewById(R.id.main_layout);
        download_logo = (ImageView) viewGroup.findViewById(R.id.download_logo);
        download_render = (ImageView) viewGroup.findViewById(R.id.download_render);
        download_guide_text = (TextView) viewGroup.findViewById(R.id.download_guide_text);
        download_progressbar = (ProgressBar) viewGroup.findViewById(R.id.download_progressbar);
        download_text = (TextView) viewGroup.findViewById(R.id.download_text);
        faq_text = (TextView) viewGroup.findViewById(R.id.faq_text);
        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.setOneShot(false);
        for (int i10 = 0; i10 < 120; i10++) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradientDrawable.setGradientRadius(MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._128sdp));
            if (i10 < 40) {
                float f10 = i10 * 0.025f;
                gradientDrawable.setColors(new int[]{((Integer) new ArgbEvaluator().evaluate(f10, -1119283065, -1283248909)).intValue(), ((Integer) new ArgbEvaluator().evaluate(f10, -1119283065, -1119283065)).intValue()});
            } else if (i10 < 80) {
                float f11 = (i10 - 40) * 0.025f;
                gradientDrawable.setColors(new int[]{((Integer) new ArgbEvaluator().evaluate(f11, -1283248909, -1119283065)).intValue(), ((Integer) new ArgbEvaluator().evaluate(f11, -1119283065, -1283248909)).intValue()});
            } else {
                float f12 = (i10 - 80) * 0.025f;
                gradientDrawable.setColors(new int[]{((Integer) new ArgbEvaluator().evaluate(f12, -1119283065, -1119283065)).intValue(), ((Integer) new ArgbEvaluator().evaluate(f12, -1283248909, -1119283065)).intValue()});
            }
            gradientDrawable.setCornerRadius(MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._4sdp));
            gradientDrawable.setStroke(MainActivity.getMainActivity().getResources().getDimensionPixelSize(R.dimen._1sdp), -8637479);
            animationDrawable.addFrame(gradientDrawable, 16);
        }
        download_progressbar.setIndeterminateDrawable(animationDrawable);
        download_progressbar.setIndeterminate(true);
        download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_5));
        SpannableString spannableString = new SpannableString("Проблемы? Мы можем вам помочь!");
        spannableString.setSpan(new StyleSpan(R.font.ttnorms_medium), 10, spannableString.length(), 33);
        spannableString.setSpan(new UnderlineSpan(), 10, spannableString.length(), 33);
        faq_text.setText(spannableString);
        faq_text.setOnClickListener(v -> {
            MainActivity.getMainActivity().faqFragment.show();
        });
        viewGroup.setVisibility(View.GONE);
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        main_layout.clearAnimation();
        main_layout.setTranslationY(f10.y);
        main_layout.animate().setDuration(300L).translationY(0.0f).start();
        download_logo.clearAnimation();
        download_logo.setTranslationY(-f10.y);
        download_logo.animate().setDuration(300L).translationY(0.0f).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        main_layout.clearAnimation();
        main_layout.setTranslationY(0.0f);
        main_layout.animate().setDuration(300L).translationY(f10.y).start();
        download_logo.clearAnimation();
        download_logo.setTranslationY(0.0f);
        download_logo.animate().setDuration(300L).translationY(-f10.y).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(0.0f).setDuration(300L).start();
        mHandler.postDelayed(new ssetVisibility(), 300L);
    }

    public class ssetVisibility implements Runnable {
        public ssetVisibility() {
        }

        @Override
        public final void run() {
            viewGroup.setVisibility(View.GONE);
        }
    }

    public void startDownload(List<String> url, List<String> path, List<String> unZip, List<String> toUnZip) {
        show();
        startAnim();

        File directory = new File(Helper.androidPath);

        // Проверка существования директории "/storage/emulated/0/Edgar"
        if (!directory.exists() || !directory.isDirectory()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Директория " + Helper.androidPath + " успешно создана.");
            } else {
                System.out.println("Ошибка при создании директории " + Helper.androidPath);
                return; // Прерываем выполнение, если директория не создана
            }
        }

        for (int i1 = 0; path.size() > i1; i1++) {
            String pat = path.get(i1);
            File f = new File(pat);
            if (f.exists()) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else if (f.isFile()) {
                    f.delete();
                    System.out.println("Файл успешно удален.");
                } else {
                    System.out.println("Не удалось определить файл или директорию.");
                }
            } else {
                System.out.println("Указанный путь не существует.");
            }
        }
        i = 0;
        urlsst = url;
        toUnnZip = toUnZip;
        unnZip = unZip;
        String pathh = unZip.get(i);
        String urlss = url.get(i);
        System.out.println(urlss);
        System.out.println(pathh);
        createDownloadTask(urlss, pathh).start();
        i++;
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
        System.out.println("Директория успешно удалена.");
    }

    /*public void CreateDownload(String url, String path) throws IOException {
        new Thread(new Runnable() {
            FileOutputStream file = new FileOutputStream(path);

            InputStream fn;
            URLConnection urlConnection = null;

            public void run() {
                try {
                    URLConnection openConnection = new URL(url).openConnection();
                    this.urlConnection = openConnection;
                    openConnection.connect();
                    int length = this.urlConnection.getContentLength();
                    System.out.println(length);
                    this.fn = this.urlConnection.getInputStream();
                    byte[] data = new byte[4096];
                    int chet = 0;
                    while (true) {
                        int read = this.fn.read(data, 0, 4096);
                        int count = read;
                        if (read == -1) {
                            break;
                        }
                        chet += count;
                        this.file.write(data, 0, count);
                        int finalChet = chet;
                        runOnUiThread(new Runnable() {
                            public final void run() {
                                Download(finalChet, length);
                            }
                        });
                    }
                    FileOutputStream fileOutputStream = this.file;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    InputStream inputStream = this.fn;
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (Exception e3) {
                    Log.e("LoaderActivity", "Ошибка " + e3);
                    FileOutputStream fileOutputStream2 = this.file;
                    if (fileOutputStream2 != null) {
                        try {
                            fileOutputStream2.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    InputStream inputStream2 = this.fn;
                    if (inputStream2 != null) {
                        try {
                            inputStream2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable th) {
                    FileOutputStream fileOutputStream3 = this.file;
                    if (fileOutputStream3 != null) {
                        try {
                            fileOutputStream3.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                    InputStream inputStream3 = this.fn;
                    if (inputStream3 != null) {
                        try {
                            inputStream3.close();
                        } catch (IOException e6) {
                            e6.printStackTrace();
                        }
                    }
                    throw th;
                }
                runOnUiThread(new Runnable() {
                    public final void run() {
                        System.out.println(urlsst.size() + " = " + i);
                        if (urlsst.size() > i) {
                            String pathh = toUnnZip.get(i);
                            String urlss = urlsst.get(i);
                            System.out.println(urlss);
                            try {
                                CreateDownload(urlss, pathh);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            i++;
                        } else {
                            i = 0;
                            if (toUnnZip.size() > i) {
                                String unn = toUnnZip.get(i);
                                String unn2 = unnZip.get(i);
                                unZip(unn, unn2);
                                i++;
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void Download(int finalChet, int length) {
        long progressPercent = finalChet * 100L / length;

        download_progressbar.setIndeterminate(false);

        download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_6, Float.valueOf(finalChet * 100.0f / length)));
        download_progressbar.setProgress((int) progressPercent);
    }*/

    private BaseDownloadTask createDownloadTask(String url, String path) {
        return FileDownloader.getImpl().create(url)
                .setPath(path, true)
                .setCallbackProgressTimes(100)
                .setMinIntervalUpdateSpeed(100)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        long progressPercent = soFarBytes * 100L / totalBytes;

                        download_progressbar.setIndeterminate(false);

                        download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_6, Float.valueOf(soFarBytes * 100.0f / totalBytes)));
                        download_progressbar.setProgress((int) progressPercent);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        Toast.makeText(MainActivity.getMainActivity(), "Произошла ошибка начните заново установку", Toast.LENGTH_SHORT).show();
                        System.out.println(e);
                        hide();
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String et, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, et, isContinue, soFarBytes, totalBytes);
                        System.out.println("dddddddd");
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        System.out.println(urlsst.size() + " = " + i);
                        if (urlsst.size() > i) {
                            String pathh = unnZip.get(i);
                            String urlss = urlsst.get(i);
                            System.out.println(urlss);
                            createDownloadTask(urlss, pathh).start();
                            i++;
                        } else {
                            i = 0;
                            if (toUnnZip.size() > i) {
                                String unn = toUnnZip.get(i);
                                String unn2 = unnZip.get(i);
                                unZip(unn, unn2);
                                i++;
                            }
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                    }
                });
    }

    public void unZip(String path, String path2) {
        String mInputFilePath = path;
        String mOutputPath = path2;
        String resultString = path.replace("/storage/emulated/0/Edgar/", "");
        download_progressbar.setProgress(100);
        download_progressbar.setIndeterminate(true);
        download_text.setText("Распаковка архивов " + resultString);
        new Thread() {
            @Override
            public void run() {
                P7ZipApi.executeCommand(String.format("7z x '%s' '-o%s' -aoa", mInputFilePath, mOutputPath));
                Utils.delete(new File(path));
                Utils.delete(new File(path + ".temp"));
                runOnUiThread(() -> {
                    if (toUnnZip.size() > i) {
                        String unn = toUnnZip.get(i);
                        String unn2 = unnZip.get(i);
                        unZip(unn, unn2);
                        i++;
                    } else {
                        MainActivity.getMainActivity().startActivity(new Intent(MainActivity.getMainActivity(), GTASA.class));
                    }
                });
            }
        }.start();
        /*try {
            ZipFile zipFile = new ZipFile(path);
            ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

            zipFile.setRunInThread(true); // Запустить процесс распаковки в отдельном потоке

            zipFile.extractAll(path2);
            while (progressMonitor.getState() == ProgressMonitor.State.BUSY) {
                // Отслеживание прогресса распаковки
                System.out.println("Прогресс: " + progressMonitor.getPercentDone() + "%");
                download_progressbar.setProgress(progressMonitor.getPercentDone());
                download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_8, Float.valueOf(progressMonitor.getPercentDone())));
                Thread.sleep(1000); // Пауза на 1 секунду
            }
            new File(path).delete();
            if (toUnnZip.size() > i) {
                String unn = toUnnZip.get(i);
                String unn2 = unnZip.get(i);
                unZip(unn, unn2);
                i++;
            }
            System.out.println("Распаковка завершена!");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*try {
            System.out.println(":LLLLLLLLLLLL");
            ZipFile zipFile = new ZipFile(path);
            zipFile.setRunInThread(true);
            zipFile.extractAll(path2);
            ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
            while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
                System.out.println(progressMonitor.getPercentDone());
                download_progressbar.setProgress(progressMonitor.getPercentDone());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        download_text.setText(MainActivity.getMainActivity().getResources().getString(R.string.launcher_donwload_info_8, Float.valueOf(progressMonitor.getPercentDone())));
                    }
                });
                //Thread.sleep(100);
            }
            new File(path).delete();
            if (toUnnZip.size() > i) {
                String unn = toUnnZip.get(i);
                String unn2 = unnZip.get(i);
                unZip(unn, unn2);
                i++;
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }*/
    }

    private static long getTotalDownloadedSize(List<String> fileDirectories) {
        long totalSize = 0;
        for (String fileDirectory : fileDirectories) {
            File directory = new File(fileDirectory);
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        totalSize += file.length();
                    }
                }
            }
        }
        return totalSize;
    }

    private void updateProgress(long downloadedSize, long totalSize) {
        download_progressbar.setIndeterminate(false);
        double result = (double) (downloadedSize * 100L) / totalSize;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedResult = decimalFormat.format(result);

        System.out.println(formattedResult); // Выводит "00.54"
        download_progressbar.setProgress((int) (downloadedSize * 100L / totalSize));

        download_text.setText("Скачивание файлов " + formattedResult + "%");
    }

    private static long getTotalFileSize(List<String> fileUrls) {
        final long[] totalSize = {0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String fileUrl : fileUrls) {
                    try {
                        URL url = new URL(fileUrl);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            totalSize[0] += url.openConnection().getContentLengthLong();
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка при получении размера файла: " + e.getMessage());
                    }
                }
            }
        }).start();
        return totalSize[0];
    }

    private static void createDirectories(List<String> fileDirectories) {
        for (String fileDirectory : fileDirectories) {
            File directory = new File(new File(fileDirectory).getParent());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
    }

    private static String getFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    @SuppressLint("NewApi")
    private static void downloadFile(String fileUrl, String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(fileUrl);
                    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    FileOutputStream fos = new FileOutputStream(filePath);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();
                    rbc.close();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void cheac(List<FilesList> fileInfoList) {
            for (FilesList file : fileInfoList) {
                System.out.println(Paths.get(new File(file.getPath()).getParent()));
                File d = new File(new File(file.getPath()).getParent());
                if (!d.exists()) {
                    if (d.mkdirs()) {
                        System.out.println("Директория создана: " + d);
                    } else {
                        System.out.println("Не удалось создать директорию: " + d);
                    }
                } else {
                    System.out.println("Директория уже существует: " + d);
                }
            }
    }

    public class UpdateImage implements Runnable {
        public UpdateImage() {
        }

        public final void run() {
            download_render.setTranslationX((float) download_render.getWidth());
            download_render.setAlpha(0.0f);
            StringBuilder v9 = new StringBuilder("render_pic_");
            v9.append(idImage);
            download_render.setImageResource(MainActivity.getMainActivity().getResources().getIdentifier(v9.toString(), "drawable", MainActivity.getMainActivity().getPackageName()));
            download_render.animate().translationX(0.0f).alpha(1.0f).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    public final void startAnim() {
        mHandler.removeCallbacksAndMessages((Object) null);
        idText = new Random().nextInt(TextInfo.length);
        idImage = 0;
        StringBuilder v9 = new StringBuilder("render_pic_");
        v9.append(idImage);
        download_render.setImageResource(MainActivity.getMainActivity().getResources().getIdentifier(v9.toString(), "drawable", MainActivity.getMainActivity().getPackageName()));
        download_guide_text.setText(TextInfo[idText]);
        download_guide_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Update();
            }
        }, 5000);
    }

    public final void Update() {
        mHandler.removeCallbacksAndMessages((Object) null);
        int i10 = idText + 1;
        idText = i10;
        int i11 = idImage + 1;
        idImage = i11;
        if (i10 >= TextInfo.length) {
            idText = 0;
        }
        if (i11 >= 6) {
            idImage = 0;
        }
        download_render.clearAnimation();
        download_render.animate().translationX((float) (-download_render.getWidth())).alpha(0.0f).setDuration(300).setInterpolator(new AccelerateInterpolator()).withEndAction(new UpdateImage()).start();
        download_guide_text.clearAnimation();
        download_guide_text.animate().translationX((float) (-download_guide_text.getWidth())).alpha(0.0f).setDuration(300).setInterpolator(new AccelerateInterpolator()).withEndAction(new textEdit()).start();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Update();
            }
        }, 5000);
    }
    public class textEdit implements Runnable {
        public textEdit() {
        }

        public final void run() {
            download_guide_text.setTranslationX((float) download_guide_text.getWidth());
            download_guide_text.setAlpha(0.0f);
            download_guide_text.setText(TextInfo[idText]);
            download_guide_text.animate().translationX(0.0f).alpha(1.0f).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
        }
    }
}

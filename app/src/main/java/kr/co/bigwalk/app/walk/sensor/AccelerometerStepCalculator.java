package kr.co.bigwalk.app.walk.sensor;

import android.hardware.SensorManager;

import java.util.Collections;
import java.util.LinkedList;

/**
 * 어떤 소스인지 사실 잘 모름... 일단 동작하기를 기도함.
 */
public class AccelerometerStepCalculator {

    private static AccelerometerStepCalculator accelerometerStepCalculator = null;

    public static AccelerometerStepCalculator getInstance(){
        if(accelerometerStepCalculator == null){
            accelerometerStepCalculator = new AccelerometerStepCalculator();
        }
        return accelerometerStepCalculator;
    }

    private AccelerometerStepCalculator(){
        initialize();
    }

    // 의미있는 peak을 추출할때 기준이 되는 최소값
    private final float DefaultMinLimitPeak = 1.315f; // 20170328 조절

    //-----------------------이 위는 고정, 아래는 고정아님
    private float lastEnergeValue;

    private LinkedList<BWEnerge> tmpEnerges;
    private BWEnerge lastEnergeForStep;

    private float currentMaxCriticalPeak = DefaultMinLimitPeak;
    private long minStepTimeInterval = 0L;

    private int tmpFindSteps = 0;

    private void initialize(){
        tmpEnerges = new LinkedList<>();
        lastEnergeForStep = new BWEnerge();

        lastEnergeValue = -1.0f;

        minStepTimeInterval = 0L;
        currentMaxCriticalPeak = DefaultMinLimitPeak;

        tmpFindSteps = 0;
    }

    public int calculateStep (float[] pValues){
        float xx = pValues[0];
        float yy = pValues[1];
        float zz = pValues[2];

        float curEnerge = (float)Math.abs(Math.sqrt(Math.pow(xx,2) + Math.pow(yy,2) + Math.pow(zz,2)));
        curEnerge -= SensorManager.STANDARD_GRAVITY;

        if (this.lastEnergeValue <= -1.0f) {
            //최초측정값
            this.lastEnergeValue = curEnerge;
            return 0;
        }

        float lastEnerge = this.lastEnergeValue;
        float deltaEnerge = Math.abs(curEnerge - lastEnerge);

        // 노이즈 필터
        // 노이즈필터에 사용 (진동폭이 해당 값을 기준으로 이 보다 커야 노이즈가 아닌것으로 인정)
        float defaultMinAmplitude = 0.1f;
        if (defaultMinAmplitude < deltaEnerge) {
            // 노이즈가 아닌것으로 판단

            lastEnergeValue = curEnerge;

        }else{

            curEnerge = lastEnerge;
        }

        // 로우패스필터
        curEnerge = lowPassFilter(curEnerge, lastEnerge);

        float peakValue = curEnerge;

        BWEnerge energeObj = new BWEnerge();

        energeObj.value = peakValue;
        energeObj.timestamp = System.currentTimeMillis();

        // 걸음을 파악
        return analysisPeaks(energeObj);
    }

    private int analysisPeaks(BWEnerge energe) {

        if (tmpEnerges == null) {
            return 0;
        }

        int limitEnergeCount = 50;
        if (limitEnergeCount > tmpEnerges.size()) {
            tmpEnerges.add(energe);
            return 0;
        }

        LinkedList<BWEnerge> targetList = new LinkedList<>(tmpEnerges);
        int energeCount = targetList.size();

        // 파악대기중인 peak값들을 제거
        tmpEnerges.clear();

        // 현재 기준이 되는 임계값을 계산
        currentMaxCriticalPeak = this.measureCriticalPeak(targetList);

        //TODO:평균 보폭시간을 구해야함
        //정해진 임계값을 기준으로 임계값을 두번씩 만나는 지점을 찾아 (상승시,하강시)하나의 peak으로 계산하고
        //각 peak사이의 간격시간을 계산
        //간격시간에 따라서 진폭과 리듬을 파악하여 행동형태를 파악한다

        int findStep = 0;

        boolean isSecond = false;
        float maxEnergeForPeak = 0.0f;

        long currentPeakEndEnergeTimestamp;

        for (int i=0; i<energeCount; i++) {

            BWEnerge tmpEnerge = targetList.get(i);

            float tmpEnergeValue = tmpEnerge.value;
            long tmpEnergetTimestamp = tmpEnerge.timestamp;

            // 현재 peak내에 유효한 보폭잠금구간의 스텝일때 다음 보복잠금구간을 갱신할때 사용될값
            maxEnergeForPeak = Math.max(tmpEnergeValue, maxEnergeForPeak);

            if (!isSecond) {

                //임계값을 처음으로 통과하는 지점 (상승, peak의 시작)
                if (currentMaxCriticalPeak <= tmpEnergeValue) {

                    isSecond = true;
                }

            }else{

                //임계값을 두번째로 통과하는 지점 (하강, peak의 끝)
                if (currentMaxCriticalPeak >= tmpEnergeValue) {

                    isSecond = false;
                    long peakDeltaTime = minStepTimeInterval;

                    currentPeakEndEnergeTimestamp = tmpEnergetTimestamp;

                    long lastPeakEndEnergeTimestamp = lastEnergeForStep.timestamp;

                    if (lastPeakEndEnergeTimestamp > 0L) {
                        peakDeltaTime = currentPeakEndEnergeTimestamp - lastPeakEndEnergeTimestamp;
                    }


                    if (minStepTimeInterval <= peakDeltaTime) {

                        lastEnergeForStep = tmpEnerge;

                        long defaultMaxPeakTimeInterval = 1000L;
                        if (defaultMaxPeakTimeInterval >= peakDeltaTime) {
                            //제한된 시간 안쪽

                            findStep++;

                            //TODO:현재의 스텝을 기준으로 다음 스텝의 validStepDeltaTime을 갱신
                            //validStepDeltaTime의 최소시간과 최대시간은 제한되어있음 (0.3 ~ 2.0)
                            //long energeDeltaTime = currentPeakEndEnergeTimestamp - energeStartTime;

                            //energeDeltaTime 이 길면 체공시간이 길다? (보폭크기)
                            //maxEnergeForPeak 이 크면 빠른이동중? (보폭시간) (정도에 따라 단계를 나눠 0.3 ~ 2.0 까지 분류)

                            minStepTimeInterval = 330L;

                        } else {
                            //제한된 시간을 벗어났을때
                            findStep = 0;
                            tmpFindSteps = 0;
                            minStepTimeInterval = 0L;
                        }
                    }

                    maxEnergeForPeak = 0.0f;
                }
            }
        }

        // 찾은 걸음 누적이 일정이상일때 걸음수 적용
        tmpFindSteps += findStep;
        if (tmpFindSteps > 0) {
            int step = tmpFindSteps;
            tmpFindSteps = 0;
            return step;
        }
        return 0;
    }


    private float measureCriticalPeak(LinkedList<BWEnerge> peaks) {

        //계속된 에너지값을 배열로 누적하고 일정구간마다(갯수 혹은 시간) 누적된 에너지들로 하이패스와 로우패스 필터링 후 나머지값으로 평균을 구해 임계값을 만든다.
        //그리고 만들어진 임계값으로 해당구간을 분석하여 걸음을 검출함

        // 정렬
        LinkedList<BWEnerge> sortedArray = new LinkedList<>(peaks);

        Collections.sort(sortedArray, (obj1, obj2) -> Float.compare(obj1.value, obj2.value));

        int sortedArrayCount = sortedArray.size();

        // 현재 누적된 구간의 임계값 구하기
        float result = 0.0f;

        //최대값두개와 최소값두개를 제거
        sortedArray.remove(sortedArrayCount - 1);
        sortedArray.remove(sortedArrayCount - 2);

        sortedArray.remove(0);
        sortedArray.remove(0);

        sortedArrayCount = sortedArray.size();

        //나머지 중간의 값으로 평균을 구함
        for (int i=0; i<sortedArrayCount; i++) {

            BWEnerge tmpEnerge = sortedArray.get(i);
            result += tmpEnerge.value;

        }

        float avgPeak = result / sortedArrayCount;

        // 최소값과 최대값을 제한
        return Math.max(avgPeak, DefaultMinLimitPeak);
    }

    private float lowPassFilter(float value, float preValue) {

        float weight = 0.8f;
        return (weight * preValue) + ((1.0f - weight) * value);
    }


    class BWEnerge implements Comparable{

        float value;
        long timestamp;


        @Override
        public int compareTo(Object another) {

            BWEnerge energe = (BWEnerge)another;

            return Float.compare(value, energe.value);
        }
    }
}

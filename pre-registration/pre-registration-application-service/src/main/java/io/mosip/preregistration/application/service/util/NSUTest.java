package io.mosip.preregistration.application.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.core.common.dto.KeyValuePairDto;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NSUTest {

    private static ObjectMapper objectMapper;

    public static void main(String[] args) {
        try {
            objectMapper = new ObjectMapper();
            createNotificationDetails("{\"id\":\"mosip.pre-registration.notification.notify\",\"request\":{\"eng\":{\"name\":\"AYIKO\",\"surname\":\"AYIKO\",\"preRegistrationId\":\"60265094394803\",\"appointmentDate\":\"2023-08-17\",\"appointmentTime\":\"09:45 AM\",\"mobNum\":null,\"emailID\":\"jerrsaf@gmail.com\",\"additionalRecipient\":true,\"isBatch\":false}},\"version\":\"1.0\",\"requesttime\":\"2023-08-17T11:34:04.921Z\"}", "eng", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MainRequestDTO<NotificationDTO> createNotificationDetails(String jsonString, String langauageCode,
                                                                     boolean isLatest)
            throws JsonParseException, JsonMappingException, io.mosip.kernel.core.exception.IOException, JSONException,
            ParseException, com.fasterxml.jackson.core.JsonParseException,
            com.fasterxml.jackson.databind.JsonMappingException, IOException {
        //log.info("sessionId", "idType", "id", "In createUploadDto method of notification service util with body " + jsonString);
        MainRequestDTO<NotificationDTO> notificationReqDto = new MainRequestDTO<>();
        JSONObject notificationData = new JSONObject(jsonString);
        JSONObject notificationDtoData = (JSONObject) notificationData.get("request");

        String surnameData = (String) ((JSONObject) notificationDtoData.get("eng")).get("surname");
        JSONObject newSurnameData = new JSONObject();
        newSurnameData.put("key", "eng");
        newSurnameData.put("value", surnameData);
        ((JSONObject)  notificationDtoData.get("eng")).put("surname", (new JSONArray()).put(newSurnameData));
        System.out.println("In createUploadDto method of notification service util updated json string " + notificationDtoData);

        NotificationDTO notificationDto = null;
        List<KeyValuePairDto<String, String>> langaueNamePairs = new ArrayList<KeyValuePairDto<String,String>>();
        if (isLatest) {
            HashMap<String, String> result = objectMapper.readValue(notificationDtoData.toString(), HashMap.class);
            KeyValuePairDto langaueNamePair = null;
            for (Map.Entry<String, String> set : result.entrySet()) {
                langaueNamePair = new KeyValuePairDto();
                notificationDto = objectMapper.convertValue(set.getValue(), NotificationDTO.class);
                langaueNamePair.setKey(set.getKey());
                langaueNamePair.setValue(notificationDto.getName());
                langaueNamePairs.add(langaueNamePair);
            }
            if (notificationDto != null) {
                notificationDto.setSurname(langaueNamePairs);
                notificationDto.setLanguageCode(langauageCode);
            }
        }
        if (!isLatest) {
            notificationDto = (NotificationDTO) JsonUtils.jsonStringToJavaObject(NotificationDTO.class,
                    notificationDtoData.toString());
            KeyValuePairDto langaueNamePair = new KeyValuePairDto();
            langaueNamePair.setKey(langauageCode);
            langaueNamePair.setValue(notificationDto.getName());
            langaueNamePairs.add(langaueNamePair);
            notificationDto.setSurname(langaueNamePairs);
            notificationDto.setLanguageCode(langauageCode);
        }

        notificationReqDto.setId(notificationData.get("id").toString());
        notificationReqDto.setVersion(notificationData.get("version").toString());
        if (!(notificationData.get("requesttime") == null
                || notificationData.get("requesttime").toString().isEmpty())) {
            notificationReqDto.setRequesttime(
                    new SimpleDateFormat("dd-MM-yyyy").parse(notificationData.get("requesttime").toString()));
        } else {
            notificationReqDto.setRequesttime(null);
        }
        notificationReqDto.setRequest(notificationDto);
        return notificationReqDto;
    }

}

#include <pebble.h>

#define HEARTRATE 0

static Window *s_window;
static TextLayer *s_text_layer;

static void send(int key, int msg) {
  DictionaryIterator *iter;
  app_message_outbox_begin(&iter);
  dict_write_int(iter, key, &msg, sizeof(int), true);
  app_message_outbox_send();
}

static void prv_on_health_data(HealthEventType type, void *context) {
  // If the update was from the Heart Rate Monitor, query it
  if (type == HealthEventHeartRateUpdate) {
    HealthValue value = health_service_peek_current_value(HealthMetricHeartRateBPM);
    // Display the heart rate
  send(HEARTRATE,value);
  }
}

static void prv_init(void) {
  bool success = health_service_set_heart_rate_sample_period(1);

  //subscribe to heart sensor updates
  health_service_events_subscribe(prv_on_health_data, NULL);


  APP_LOG(APP_LOG_LEVEL_DEBUG, "Monitor heart rate 1sec: %p", success);
}

static void prv_deinit(void) {
  bool success = health_service_set_heart_rate_sample_period(0);
  APP_LOG(APP_LOG_LEVEL_DEBUG, "Monitor heart rate 1sec: %p", success);
}

int main(void) {
  prv_init();

  //APP_LOG(APP_LOG_LEVEL_DEBUG, "Done initializing, pushed window: %p", s_window);

  app_event_loop();
  prv_deinit();
}




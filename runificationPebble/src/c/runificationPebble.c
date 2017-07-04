#include <pebble.h>

#define HEARTRATE 0


static Window *s_main_window;
static TextLayer *s_output_layer;

static void send(int key, int msg) {
  DictionaryIterator *iter;
  app_message_outbox_begin(&iter);
  dict_write_int(iter, key, &msg, sizeof(int), true);
  app_message_outbox_send();
}

static void outbox_sent_handler(DictionaryIterator *iter, void *context) {
  // Ready for next command
}

static void prv_on_health_data(HealthEventType type, void *context) {
  // If the update was from the Heart Rate Monitor, query it
  if (type == HealthEventHeartRateUpdate) {
    HealthValue value = health_service_peek_current_value(HealthMetricHeartRateBPM);
    // Display the heart rate
    APP_LOG(APP_LOG_LEVEL_DEBUG, "HR Report %p", value);

  static char s_hrm_buffer[8];
  snprintf(s_hrm_buffer, sizeof(s_hrm_buffer), "%lu BPM", (uint32_t)value);
  text_layer_set_text(s_output_layer, s_hrm_buffer);
  send(HEARTRATE,value);
  }
}



static void main_window_unload(Window *window) {
  text_layer_destroy(s_output_layer);
}

static void outbox_failed_handler(DictionaryIterator *iter, AppMessageResult reason, void *context) {
  text_layer_set_text(s_output_layer, "Send failed!");
  APP_LOG(APP_LOG_LEVEL_ERROR, "Fail reason: %d", (int)reason);
}

static void main_window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);

  const int text_height = 20;
  const GEdgeInsets text_insets = GEdgeInsets((bounds.size.h - text_height) / 2, 0);

  s_output_layer = text_layer_create(grect_inset(bounds, text_insets));
  text_layer_set_text(s_output_layer, "Press up or down.");
  text_layer_set_text_alignment(s_output_layer, GTextAlignmentCenter);
  layer_add_child(window_layer, text_layer_get_layer(s_output_layer));
}





static void prv_init(void) {

  s_main_window = window_create();
  
  window_set_window_handlers(s_main_window, (WindowHandlers) {
    .load = main_window_load,
    .unload = main_window_unload,
  });
  window_stack_push(s_main_window, true);

  // Open AppMessage
  app_message_register_outbox_sent(outbox_sent_handler);
  app_message_register_outbox_failed(outbox_failed_handler);
  
  const int inbox_size = 128;
  const int outbox_size = 128;
  app_message_open(inbox_size, outbox_size);


  bool success = health_service_set_heart_rate_sample_period(1);

  //subscribe to heart sensor updates
  health_service_events_subscribe(prv_on_health_data, NULL);


  APP_LOG(APP_LOG_LEVEL_DEBUG, "Monitor heart rate 1sec: %p", success);
}

static void prv_deinit(void) {
  bool success = health_service_set_heart_rate_sample_period(0);
  APP_LOG(APP_LOG_LEVEL_DEBUG, "Monitor heart rate ti normal: %p", success);

  window_destroy(s_main_window);
}

int main(void) {
  prv_init();
  APP_LOG(APP_LOG_LEVEL_DEBUG, "Done initializing, pushed window: %p", s_main_window);

  //APP_LOG(APP_LOG_LEVEL_DEBUG, "Done initializing, pushed window: %p", s_window);

  app_event_loop();
  prv_deinit();
}




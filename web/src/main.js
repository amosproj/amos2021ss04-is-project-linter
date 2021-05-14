import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import vuetify from "./plugins/vuetify";
import { TinyEmitter } from "tiny-emitter";

Vue.config.productionTip = false;

Vue.prototype.$em = new TinyEmitter();

new Vue({
	router,
	vuetify,
	render: (h) => h(App),
}).$mount("#app");

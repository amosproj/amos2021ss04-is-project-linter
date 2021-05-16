import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
	{
		path: "/",
		name: "Dashboard",
		component: () => import("@/views/Dashboard.vue"),
		children: [
			{
				path: "/projects",
				name: "Projects",
				component: () => import("@/views/Projects.vue"),
			},
			{
				path: "/stats",
				name: "Stats",
				component: () => import("@/views/Stats.vue"),
			},
		],
	},
	{
		path: "/result/:id",
		name: "Result",
		component: () => import("@/views/Result.vue"),
	},
];

const router = new VueRouter({
	mode: "history",
	base: process.env.BASE_URL,
	routes,
});

export default router;

<template>
	<v-app>
		<TheHeader />

		<v-main>
			<v-container>
				<Events />
			</v-container>
			<router-view />
		</v-main>

		<TheFooter />
	</v-app>
</template>

<script>
import TheHeader from "./components/TheHeader.vue";
import Events from "./components/Events.vue";
import TheFooter from './components/TheFooter.vue';

export default {
	components: { TheHeader, Events, TheFooter },
	name: "App",
};
</script>

<template>
	<v-app>
		<TheHeader />

		<v-main>
			<v-container>
				<Events />
			</v-container>
			<router-view />
		</v-main>
	</v-app>
</template>

<script>
import TheHeader from "./components/TheHeader.vue";
import Events from "./components/Events.vue";

export default {
	components: { TheHeader, Events },
	name: "App",
};
</script>

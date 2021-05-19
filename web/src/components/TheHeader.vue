<template>
	<v-app-bar app color="primary" flat>
		<v-container class="py-0 fill-height">
			<v-btn text :to="{ name: 'Dashboard' }">
				ACIDIC
			</v-btn>

			<v-spacer></v-spacer>

			<v-responsive max-width="260">
				<v-text-field dense flat hide-details rounded solo-inverted placeholder="Suche ..."></v-text-field>
			</v-responsive>
		</v-container>
	</v-app-bar>
</template>

<script>
export default {
	name: "TheHeader",
};
</script>

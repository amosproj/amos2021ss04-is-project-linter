<template>
	<v-container>
		<v-list>
			<v-list-item v-for="res in results" :key="res.id">
				<v-list-item-avatar>
					<v-icon class="yellow" dark>mdi-triangle</v-icon>
				</v-list-item-avatar>

				<v-list-item-content>
					<v-list-item-title v-text="res.checkName"></v-list-item-title>
					<!-- <v-list-item-subtitle>bbb</v-list-item-subtitle> -->
				</v-list-item-content>

				<v-list-item-action>
					<v-btn icon>
						<v-icon color="grey lighten-1">mdi-information</v-icon>
					</v-btn>
				</v-list-item-action>
			</v-list-item>
		</v-list>
	</v-container>
</template>

<script>
export default {
	name: "Result",
	data() {
		return {
			results: [
				{
					checkName: "guestRoleEnabled",
					id: 66,
					lintId: 64,
					result: false,
					severity: "MEDIUM",
				},
				{
					checkName: "developerRoleEnabled",
					id: 68,
					lintId: 64,
					result: false,
					severity: "MEDIUM",
				},
				{
					checkName: "checkReadmeExistence",
					id: 70,
					lintId: 64,
					result: false,
					severity: "MEDIUM",
				},
				{
					checkName: "checkContributingExistence",
					id: 72,
					lintId: 64,
					result: false,
					severity: "MEDIUM",
				},
				{
					checkName: "hasMergeRequestEnabled",
					id: 74,
					lintId: 64,
					result: true,
					severity: "MEDIUM",
				},
				{
					checkName: "isPublic",
					id: 76,
					lintId: 64,
					result: true,
					severity: "MEDIUM",
				},
				{
					checkName: "gitlabPagesEnabled",
					id: 78,
					lintId: 64,
					result: false,
					severity: "MEDIUM",
				},
			],
		};
	},
};
</script>
